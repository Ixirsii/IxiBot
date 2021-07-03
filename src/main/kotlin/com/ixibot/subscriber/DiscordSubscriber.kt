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
import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.reaction.ReactionEmoji
import reactor.core.Disposable
import java.util.Optional

/**
 * Subscribe to events which trigger Discord actions.
 *
 * @author Ryan Porterfield
 */
class DiscordSubscriber(
    /** Database interface. */
    private val database: Database
) : Logging by LoggingImpl<DiscordSubscriber>() {

    // TODO: Add documentation
    @Throws(IllegalArgumentException::class)
    private fun getFilter(
        channelID: Snowflake,
        messageID: Snowflake,
        optionalCustom: Optional<ReactionEmoji.Custom>,
        optionalUnicode: Optional<ReactionEmoji.Unicode>
    ): (reaction: RoleReaction) -> Boolean {
        return when {
            optionalCustom.isPresent -> {
                { reaction: RoleReaction ->
                    val custom: ReactionEmoji.Custom = optionalCustom.get()
                    (reaction.messageID == messageID &&
                            reaction.channelID == channelID &&
                            reaction.reactionEmojiName == custom.name &&
                            reaction.boxedReactionEmojiID == custom.id.asLong())
                }
            }
            optionalUnicode.isPresent -> {
                { reaction: RoleReaction ->
                    val unicode = optionalUnicode.get()
                    (reaction.messageID == messageID &&
                            reaction.channelID == channelID &&
                            reaction.reactionEmojiName == unicode.raw)
                }
            }
            else -> {
                throw IllegalArgumentException("One of either optionalCustom or optionalUnicode must be present")
            }
        }
    }

    // TODO: Add documentation
    private fun getMemberConsumer(
        isAdd: Boolean,
        messageID: Long,
        reactionEmoji: ReactionEmoji,
        roleID: Snowflake
    ): (Member) -> Disposable {

        val reasonFormat: String = getReasonFormat(isAdd)

        return { member: Member ->
            val reason: String = String.format(
                reasonFormat,
                member.displayName,
                messageID,
                reactionEmoji,
                roleID
            )
            log.info(reason)
            if (isAdd) {
                member.addRole(roleID, reason).subscribe()
            } else {
                member.removeRole(roleID, reason).subscribe()
            }
        }
    }

    // TODO: Add documentation
    private fun getReasonFormat(isAdd: Boolean): String {
        return if (isAdd)
            "User %s reacted to message %d with %s to get role %s."
        else
            "User %s reacted to message %d with %s to remove role %s."
    }

    /**
     * DiscordReactionEvent subscriber.
     *
     * @param event Event published to the event bus.
     */
    @Subscribe
    fun onDiscordReactionEvent(event: DiscordReactionEvent) {
        val filter: (reaction: RoleReaction) -> Boolean = try {
            getFilter(
                channelID = event.channelID,
                messageID = event.messageID,
                optionalCustom = event.reactionEmoji.asCustomEmoji(),
                optionalUnicode = event.reactionEmoji.asUnicodeEmoji()
            )
        } catch (iae: IllegalArgumentException) {
            log.error(
                "Failed to get reaction that user added to message.\nUser: {}\nMessage: {}\nEmoji: {}",
                event.userID,
                event.messageID,
                event.reactionEmoji,
                iae
            )
            return
        }

        val reactionOptional: Optional<RoleReaction> = database.allRoleReactions.stream().filter(filter).findFirst()
        val (_, _, _, _, _, reactionEmoji, roleID) = reactionOptional.get()

        if (reactionOptional.isPresent) {
            event.messageMono.subscribe { message: Message ->
                message.authorAsMember.subscribe {
                    getMemberConsumer(event.isAdd, message.id.asLong(), reactionEmoji, roleID)
                }
            }
        }
    }
}
