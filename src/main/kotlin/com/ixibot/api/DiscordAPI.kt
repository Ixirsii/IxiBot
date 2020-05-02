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

package com.ixibot.api

import com.ixibot.IxiBot
import com.ixibot.Logging
import com.ixibot.LoggingImpl
import com.ixibot.data.RoleReaction
import com.ixibot.listener.DiscordListener
import discord4j.core.DiscordClient
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.User
import discord4j.core.`object`.util.Snowflake
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.event.domain.message.ReactionAddEvent
import discord4j.core.event.domain.message.ReactionRemoveEvent
import java.net.ConnectException

/**
 * Discord4J wrapper.
 *
 * @author Ryan Porterfield
 */
class DiscordAPI constructor(
        /**
         * Discord client.
         */
        private val discordClient: DiscordClient,
        /**
         * Listener for Discord messages and events.
         */
        private val discordListener: DiscordListener,
        /**
         * If `true` this API wrapper will throw an exception on failure to connect.
         */
        private val isDiscordRequired: Boolean = false
) : Logging by LoggingImpl<IxiBot>() {

    /**
     * Initialize Discord API.
     *
     * @throws ConnectException on failure to connect to API.
     */
    @Throws(ConnectException::class)
    fun init() {
        registerDiscordListeners()
        discordClient.login().block()
        if (isDiscordRequired && !discordClient.isConnected) {
            throw ConnectException("Failed to connect to Discord API")
        }
    }

    /**
     * Stop the bot and clean up resources.
     */
    fun logout() {
        log.debug("Logging out of Discord")
        if (discordClient.isConnected) {
            discordClient.logout().block()
        }
    }

    /**
     * Register Discord event listeners.
     */
    private fun registerDiscordListeners() {
        discordClient.eventDispatcher.on(MessageCreateEvent::class.java)
                .subscribe { event: MessageCreateEvent? -> discordListener.messageCreateListener(event!!) }
        discordClient.eventDispatcher.on(ReactionAddEvent::class.java)
                .subscribe { event: ReactionAddEvent? -> discordListener.reactionAddListener(event!!) }
        discordClient.eventDispatcher.on(ReactionRemoveEvent::class.java)
                .subscribe { event: ReactionRemoveEvent? -> discordListener.reactionRemoveListener(event!!) }
    }

    /**
     * Check all role assignment reactions and update roles for all members accordingly.
     *
     * @param reactionsMap Map of guild ID to list of role reactions in that guild.
     */
    fun updateAllRoles(reactionsMap: Map<Snowflake, List<RoleReaction>>) {
        // TODO: Clean this function up
        reactionsMap.forEach { (guildID: Snowflake, verifiedReactions: List<RoleReaction>) ->
            discordClient.getGuildById(guildID)
                    .subscribe { guild: Guild ->
                        val members = guild.members.collectList().block()
                        for (reaction in verifiedReactions) {
                            discordClient.getMessageById(
                                    reaction.channelID,
                                    reaction.messageID)
                                    .subscribe { message: Message ->
                                        updateReactionRoles(guild, members, message, reaction)
                                    }
                        }
                    }
        }
    }

    /**
     * Verify all role assignments for a message reaction.
     *
     * @param guild Guild containing message.
     * @param members Members in the guild.
     * @param message Message containing reaction.
     * @param verifiedReaction Role assignment reaction.
     */
    private fun updateReactionRoles(guild: Guild,
                                    members: List<Member>,
                                    message: Message,
                                    verifiedReaction: RoleReaction) {
        val reactors = message.getReactors(verifiedReaction.reactionEmoji)
                .collectList()
                .block()
        if (reactors == null) {
            log.error("Unable to get list of reactors for message {}", message.id)
            return
        }
        if (verifiedReaction.isAddVerified) {
            verifyAddRoles(guild, reactors, message, verifiedReaction)
        }
        if (verifiedReaction.isRemoveVerified) {
            verifyRemoveRoles(members, reactors, message, verifiedReaction)
        }
    }

    /**
     * Verify that users who have added a role reaction have the role added.
     *
     * @param guild Guild containing message.
     * @param reactors Users who have reacted with the reaction to the message.
     * @param message Message containing reaction.
     * @param verifiedReaction Role assignment reaction.
     */
    private fun verifyAddRoles(
            guild: Guild,
            reactors: List<User>,
            message: Message,
            verifiedReaction: RoleReaction) {
        for (reactor in reactors) {
            reactor.asMember(guild.id).subscribe { member: Member ->
                if (!member.roleIds.contains(verifiedReaction.roleID)) {
                    val addRoleReason = String.format(
                            "User %s reacted to message %d with reaction %s but did not have role.",
                            member.displayName,
                            message.id.asLong(),
                            verifiedReaction.reactionEmoji)
                    log.info(addRoleReason)
                    member.addRole(verifiedReaction.roleID, addRoleReason).subscribe()
                }
            }
        }
    }

    /**
     * Verify that users who have not added a role reaction don't have the role.
     *
     * @param members Members in the guild.
     * @param reactors Users who have reacted with the reaction to the message.
     * @param message Message containing reaction.
     * @param verifiedReaction Role assignment reaction.
     */
    private fun verifyRemoveRoles(
            members: List<Member>,
            reactors: List<User>,
            message: Message,
            verifiedReaction: RoleReaction) {
        for (member in members) {
            if (member.roleIds.contains(verifiedReaction.roleID)) {
                val optionalReactor = reactors.stream()
                        .filter { user: User -> user.id == member.id }
                        .findAny()
                if (!optionalReactor.isPresent) {
                    val removeRoleReason = String.format(
                            "User %s had not reacted to message %d with reaction %s but had role.",
                            member.displayName,
                            message.id.asLong(),
                            verifiedReaction.reactionEmoji)
                    log.info(removeRoleReason)
                    member.removeRole(verifiedReaction.roleID, removeRoleReason).subscribe()
                }
            }
        }
    }
}