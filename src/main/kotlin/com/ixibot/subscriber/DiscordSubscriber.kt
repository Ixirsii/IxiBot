/*
 * Copyright (c) 2020, Ryan Porterfield
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *
 *     3. Neither the name of the copyright holder nor the names of its
 *        contributors may be used to endorse or promote products derived from
 *        this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 *  IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *  PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 *  HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.ixibot.subscriber

import com.google.common.eventbus.Subscribe
import com.ixibot.Logging
import com.ixibot.LoggingImpl
import com.ixibot.data.RoleReaction
import com.ixibot.database.Database
import com.ixibot.event.DiscordReactionEvent
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.reaction.ReactionEmoji
import java.util.Optional
import java.util.function.Predicate

class DiscordSubscriber(
        /** Database interface. */
        private val database: Database) : Logging by LoggingImpl<DiscordSubscriber>() {

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
            optionalCustom.isPresent  -> {
                val custom: ReactionEmoji.Custom = optionalCustom.get()
                Predicate { reaction: RoleReaction ->
                    (reaction.messageID == event.messageID &&
                            reaction.channelID == event.channelID &&
                            reaction.reactionEmojiName == custom.name &&
                            reaction.boxedReactionEmojiID == custom.id.asLong())
                }
            }
            optionalUnicode.isPresent -> {
                val unicode = optionalUnicode.get()
                Predicate { reaction: RoleReaction ->
                    (reaction.messageID == event.messageID &&
                            reaction.channelID == event.channelID &&
                            reaction.reactionEmojiName == unicode.raw)
                }
            }
            else                      -> {
                log.error(
                        "Failed to get reaction that user added to message." +
                                "\nUser: {}\nMessage: {}\nEmoji: {}",
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
            val reasonFormat = if (event.isAdd)
                "User %s reacted to message %d with %s to get role %s."
            else
                "User %s reacted to message %d with %s to remove role %s."
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
}
