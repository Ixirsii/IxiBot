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

import com.ixibot.api.DiscordAPI
import com.ixibot.data.RoleReaction
import com.ixibot.database.Database
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.net.ConnectException
import java.sql.SQLException
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

/**
 * Bot configuration file.
 */
const val CONFIG_FILE_NAME = "config.yaml"

/**
 * Bot configuration directory.
 */
const val CONFIG_DIRECTORY = "config/"

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
    /** Database interface. */
    private val database: Database,
    /** Discord API interface. */
    private val discordAPI: DiscordAPI,
    /** Interval (in minutes) between Discord role verification checks. */
    private val roleVerifyDelay: Long,
    /**
     * Thread pool executor for scheduled async actions.
     *
     * TODO: Replace this with coroutines
     */
    private val scheduler: ScheduledExecutorService
) : AutoCloseable, Logging by LoggingImpl<IxiBot>() {

    /**
     * `true` while bot is running, `false` when bot is terminating.
     */
    private var running = false

    /**
     * {@inheritDoc}
     */
    override fun close() {
        log.trace("Shutting down bot")
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
        running = true
    }

    /**
     * Run the bot.
     */
    fun run() {
        scheduler.scheduleAtFixedRate(
            {
                val roleReactions: List<RoleReaction> = database.allRoleReactions
                discordAPI.updateAllRoles(
                    roleReactions.stream()
                        .filter(RoleReaction::isVerified)
                        .collect(Collectors.groupingBy(RoleReaction::guildID))
                )
            },
            0,
            roleVerifyDelay,
            TimeUnit.MINUTES
        )

        // Keep main thread alive
        while (running) {
            runBlocking {
                delay(1000L)
            }
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
