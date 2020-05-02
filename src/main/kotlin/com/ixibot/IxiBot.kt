/*
 * Copyright (c) 2019, Ryan Porterfield
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *     1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *     3. Neither the name of the copyright holder nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.ixibot

import com.google.common.eventbus.Subscribe
import com.ixibot.api.DiscordAPI
import com.ixibot.data.RoleReaction
import com.ixibot.database.Database
import com.ixibot.event.DiscordReactionEvent
import com.ixibot.event.RoleReactionEvent
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.reaction.ReactionEmoji
import java.net.ConnectException
import java.sql.SQLException
import java.util.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.function.Predicate
import java.util.stream.Collectors

/**
 * Bot configuration file.
 */
private const val CONFIG_FILE_NAME = "config.yaml"

/**
 * Bot configuration directory.
 */
const val CONFIG_DIRECTORY = "config/"

/**
 * File path to bot configuration resource.
 */
const val CONFIG_RESOURCE = "/$CONFIG_FILE_NAME"

/**
 * Config file configured by the user.
 */
const val USER_CONFIG_FILE = CONFIG_DIRECTORY + CONFIG_FILE_NAME

/**
 * Bot logic class once startup is complete and user configuration is loaded.
 *
 * @author Ryan Porterfield
 */
class IxiBot(
        /**
         * Database interface.
         */
        private val database: Database,
        /**
         * Discord API interface.
         */
        private val discordAPI: DiscordAPI,
        /**
         * Interval (in minutes) between Discord role verification checks.
         */
        private val roleVerifyDelay: Long,
        /**
         * Thread pool executor for scheduled async actions.
         */
        private val scheduler: ScheduledExecutorService
) : AutoCloseable, Logging by LoggingImpl<IxiBot>(), Runnable {

    /**
     * `true` while bot is running, `false` when bot is terminating.
     */
    private var running = false

    /**
     * {@inheritDoc}
     */
    override fun close() {
        log.trace("Shutting down bot")
        discordAPI.logout()
        shutdownScheduler()
        try {
            database.close()
        } catch (sqle: SQLException) {
            log.error("Caught SQLException while attempting to close database", sqle)
        }
    }

    /**
     * Initialize bot instance.
     *
     * @throws ConnectException on failure to connect to API.
     */
    @Throws(ConnectException::class)
    fun init() {
        discordAPI.init()
        running = true
    }

    // TODO: Fix this
    /**
     * DiscordReactionEvent subscriber.
     *
     * @param event Event published to the event bus.
     */
    @Subscribe
    fun onDiscordReactionEvent(event: DiscordReactionEvent) {
        val filter: Predicate<RoleReaction>
        val optionalCustom: Optional<ReactionEmoji.Custom> = event.reactionEmoji
                .asCustomEmoji()
        val optionalUnicode = event.reactionEmoji
                .asUnicodeEmoji()
        filter = when {
            optionalCustom.isPresent -> {
                val custom: ReactionEmoji.Custom = optionalCustom.get()
                Predicate { reaction: RoleReaction ->
                    (reaction.messageID == event.messageID
                            && reaction.channelID == event.channelID
                            && reaction.reactionEmojiName == custom.name
                            && reaction.boxedReactionEmojiID == custom.id.asLong())
                }
            }
            optionalUnicode.isPresent -> {
                val unicode = optionalUnicode.get()
                Predicate { reaction: RoleReaction ->
                    (reaction.messageID == event.messageID
                            && reaction.channelID == event.channelID
                            && reaction.reactionEmojiName == unicode.raw)
                }
            }
            else -> {
                log.error("Failed to get reaction that user added to message."
                        + "\nUser: {}\nMessage: {}\nEmoji: {}",
                        event.userID,
                        event.messageID,
                        event.reactionEmoji)
                return
            }
        }
        val reactionOptional = database.allRoleReactions.stream()
                .filter(filter)
                .findFirst()
        if (reactionOptional.isPresent) {
            val reasonFormat = if (event.isAdd) "User %s reacted to message %d with %s to get role %s." else "User %s reacted to message %d with %s to remove role %s."
            val (_, _, _, _, _, reactionEmoji, roleID) = reactionOptional.get()
            event.messageMono.subscribe { message: Message ->
                message.authorAsMember.subscribe { member: Member ->
                    val reason = String.format(
                            reasonFormat,
                            member.displayName,
                            message.id.asLong(),
                            reactionEmoji,
                            roleID)
                    log.info(reason)
                    if (event.isAdd) {
                        member.addRole(roleID, reason)
                    } else {
                        member.removeRole(roleID, reason)
                    }
                }
            }
        }
    }

    /**
     * RoleReactionEvent subscriber.
     *
     * @param event Event published to event bus.
     */
    @Subscribe
    fun onRoleReactionEvent(event: RoleReactionEvent) {
        val roleReaction = event.roleReaction
        if (event.isCreate) {
            database.addRoleReaction(roleReaction);
        } else {
            database.deleteRoleReaction(roleReaction);
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun run() {
        scheduler.scheduleAtFixedRate(
                {
                    val roleReactions: List<RoleReaction> = database.allRoleReactions
                    discordAPI.updateAllRoles(roleReactions.stream()
                            .filter(RoleReaction::isVerified)
                            .collect(Collectors.groupingBy(RoleReaction::guildID)))
                },
                0,
                roleVerifyDelay,
                TimeUnit.MINUTES)
        try {
            while (running) { // Sleep for 1 second (1s * 1000ms/s = 1000ms)
                Thread.sleep(1000)
            }
        } catch (ie: InterruptedException) {
            log.error(
                    "Thread \"{}\" interrupted while sleeping.",
                    Thread.currentThread().name,
                    ie)
        }
    }

    /**
     * Shutdown thread pool scheduler.
     */
    private fun shutdownScheduler() {
        scheduler.shutdown()
        // Wait for the thread pool to shut down
        try { // Wait for tasks to terminate
            if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) { // (Attempt to) force stop thread pool
                scheduler.shutdownNow()
                // Wait for tasks to respond to being cancelled
                if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                    log.error("Failed to shut down thread pool")
                }
            }
        } catch (ie: InterruptedException) {
            log.error("Thread interrupted while waiting for thread pool to shutdown", ie)
            // Verify that thread pool is shutdown
            scheduler.shutdownNow()
            // Preserve interrupt status
            Thread.currentThread().interrupt()
        }
    }
}
