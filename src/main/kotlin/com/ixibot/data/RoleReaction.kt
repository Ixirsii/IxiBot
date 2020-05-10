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

package com.ixibot.data

import discord4j.core.`object`.reaction.ReactionEmoji
import discord4j.core.`object`.util.Snowflake
import java.util.Optional

/**
 * Role assignment reaction POJO.
 *
 * @author Ryan Porterfield
 */
data class RoleReaction constructor(
        /** Channel ID containing the message. */
        val channelID: Snowflake,
        /** Guild ID containing the channel. */
        val guildID: Snowflake,
        /**
         * Is the role add verified.
         *
         * If this is true, the bot will periodically check all users who have reacted with this
         * reaction and assign them the role if they don't have it.
         */
        val isAddVerified: Boolean = false,
        /**
         * Is the role remove verified.
         *
         * If this is true, the bot will periodically check all users and remove the role from
         * all users in the guild who haven't reacted with this reaction.
         */
        val isRemoveVerified: Boolean = false,
        /** Message ID containing the reaction. */
        val messageID: Snowflake,
        /** Reaction emoji name/raw. */
        val reactionEmoji: ReactionEmoji,
        /** Role ID to (un)assign. */
        val roleID: Snowflake) {

    /**
     * Get boxed reaction emoji ID.
     *
     * @return reaction emoji ID if reaction emoji is a custom emoji, otherwise null.
     */
    val boxedReactionEmojiID: Long?
        get() {
            val optionalCustom: Optional<ReactionEmoji.Custom> = reactionEmoji.asCustomEmoji()
            return if (optionalCustom.isPresent) {
                optionalCustom.get().id.asLong()
            } else {
                null
            }
        }

    /**
     * Get the name (unicode raw or custom name) of the reaction emoji.
     *
     * @return name of the reaction emoji.
     */
    val reactionEmojiName: String
        get() {
            val optionalCustom: Optional<ReactionEmoji.Custom> = reactionEmoji.asCustomEmoji()
            val optionalUnicode = reactionEmoji.asUnicodeEmoji()
            return optionalCustom.map<String> { it.name }
                    .orElseGet { optionalUnicode.map<String> { it.raw }.orElse("") }
        }

    /**
     * Is the role add or remove verified.
     *
     * @return `true` if either addVerified or removeVerified is `true`,
     * otherwise `false`.
     * @see RoleReaction.isAddVerified
     * @see RoleReaction.isRemoveVerified
     */
    val isVerified: Boolean
        get() = isAddVerified || isRemoveVerified
}
