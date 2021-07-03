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

import com.ixibot.Logging
import com.ixibot.LoggingImpl
import com.ixibot.data.RoleReaction
import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.User
import reactor.core.publisher.Mono

/**
 * Discord4J wrapper.
 *
 * @author Ryan Porterfield
 */
class DiscordAPI(
    /** Discord client. */
    private val discordClient: GatewayDiscordClient
) : Logging by LoggingImpl<DiscordAPI>() {

    /**
     * Check all role assignment reactions and update roles for all members accordingly.
     *
     * @param reactionsMap Map of guild ID to list of role reactions in that guild.
     */
    fun updateAllRoles(reactionsMap: Map<Snowflake, List<RoleReaction>>) {
        reactionsMap.forEach { (guildID: Snowflake, verifiedReactions: List<RoleReaction>) ->
            discordClient.getGuildById(guildID).subscribe { guild: Guild -> updateGuildRoles(guild, verifiedReactions) }
        }
    }

    /**
     * Verify all role assignments for a guild.
     *
     * @param guild Guild to verify reactions in.
     * @param verifiedReactions List of role reactions to verify.
     */
    private fun updateGuildRoles(guild: Guild, verifiedReactions: List<RoleReaction>) {
        for (reaction in verifiedReactions) {
            discordClient.getMessageById(reaction.channelID, reaction.messageID)
                .subscribe { message: Message -> updateReactionRoles(guild, message, reaction) }
        }
    }

    /**
     * Verify all role assignments for a message reaction.
     *
     * @param guild Guild containing message.
     * @param message Message containing reaction.
     * @param verifiedReaction Role assignment reaction.
     */
    private fun updateReactionRoles(
        guild: Guild,
        message: Message,
        verifiedReaction: RoleReaction
    ) {
        message.getReactors(verifiedReaction.reactionEmoji)
            .collectList()
            .subscribe { reactors: List<User> ->
                if (verifiedReaction.isAddVerified) {
                    verifyAddRoles(guild.id, reactors, message, verifiedReaction)
                }
                if (verifiedReaction.isRemoveVerified) {
                    guild.members.collectList().subscribe { members: List<Member> ->
                        verifyRemoveRoles(members, reactors, message, verifiedReaction)
                    }
                }
            }
    }

    /**
     * Verify that users who have added a role reaction have the role added.
     *
     * @param guildId Guild containing message.
     * @param reactors Users who have reacted with the reaction to the message.
     * @param message Message containing reaction.
     * @param verifiedReaction Role assignment reaction.
     */
    private fun verifyAddRoles(
        guildId: Snowflake,
        reactors: List<User>,
        message: Message,
        verifiedReaction: RoleReaction
    ) {
        reactors.stream()
            .map { reactor: User -> reactor.asMember(guildId) }
            .forEach { memberMono: Mono<Member> ->
                memberMono.subscribe { member: Member ->
                    if (!member.roleIds.contains(verifiedReaction.roleID)) {
                        val addRoleReason = String.format(
                            "User %s reacted to message %d with reaction %s but did not have role.",
                            member.displayName,
                            message.id.asLong(),
                            verifiedReaction.reactionEmoji
                        )
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
        verifiedReaction: RoleReaction
    ) {
        members.stream()
            .filter { member: Member -> member.roleIds.contains(verifiedReaction.roleID) }
            .filter { member: Member ->
                !reactors.stream().filter { user: User -> user.id == member.id }.findAny().isPresent
            }
            .forEach { member: Member ->
                val removeRoleReason: String = String.format(
                    "User %s had not reacted to message %d with reaction %s but had role.",
                    member.displayName,
                    message.id.asLong(),
                    verifiedReaction.reactionEmoji
                )
                log.info(removeRoleReason)
                member.removeRole(verifiedReaction.roleID, removeRoleReason).subscribe()
            }
    }
}
