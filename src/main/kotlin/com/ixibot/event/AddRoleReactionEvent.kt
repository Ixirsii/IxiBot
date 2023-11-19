/*
 * Copyright (c) 2021, Ryan Porterfield
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

package com.ixibot.event

import discord4j.common.util.Snowflake
import discord4j.core.`object`.reaction.ReactionEmoji

/**
 * Create role reaction pub/sub event.
 */
class AddRoleReactionEvent(
    /** Channel ID containing the message. */
    val channelID: Snowflake?, isHelp: Boolean, isValid: Boolean,
    /** Was the verify flag passed? */
    val isVerify: Boolean,
    /** Was the verify_add flag passed? */
    val isVerifyAdd: Boolean,
    /** Was the verify_remove flag passed? */
    val isVerifyRemove: Boolean,
    /** Message ID containing the reaction. */
    val messageID: Snowflake?,
    /** Reaction emoji name/raw. */
    val reactionEmoji: ReactionEmoji?,
    /** Role ID to (un)assign. */
    val roleID: Snowflake?
) : CommandEvent<AddRoleReactionEvent, AddRoleReactionEvent.Builder>(isHelp = isHelp, isValid = isValid) {

    /**
     * Get a Builder pre-populated with the values in this event.
     *
     * @return a Builder pre-populated with the values in this event.
     */
    override fun toBuilder(): Builder {
        return Builder()
    }

    /**
     * Builder class for AddRoleReactionEvent.
     */
    data class Builder(
        var channelID: Snowflake? = null,
        var isVerify: Boolean = false,
        var isVerifyAdd: Boolean = false,
        var isVerifyRemove: Boolean = false,
        var messageID: Snowflake? = null,
        var reactionEmoji: ReactionEmoji? = null,
        var roleID: Snowflake? = null
    ) : CommandEvent.Builder<AddRoleReactionEvent, Builder>() {

        override fun build(): AddRoleReactionEvent {
            return AddRoleReactionEvent(
                channelID = channelID,
                isHelp = isHelp,
                isValid = isValid,
                isVerify = isVerify,
                isVerifyAdd = isVerifyAdd,
                isVerifyRemove = isVerifyRemove,
                messageID = messageID,
                reactionEmoji = reactionEmoji,
                roleID = roleID
            )
        }

        override fun self(): Builder {
            return this
        }

        fun channelID(channelID: Snowflake): Builder = apply { this.channelID = channelID }

        fun isVerify(verify: Boolean): Builder = apply { this.isVerify = verify }

        fun isVerifyAdd(verifyAdd: Boolean): Builder = apply { this.isVerifyAdd = verifyAdd }

        fun isVerifyRemove(verifyRemove: Boolean): Builder = apply { this.isVerifyRemove = verifyRemove }

        fun messageID(messageID: Snowflake): Builder = apply { this.messageID = messageID }

        fun reactionEmoji(reactionEmoji: ReactionEmoji): Builder = apply { this.reactionEmoji = reactionEmoji }

        fun roleID(roleID: Snowflake): Builder = apply { this.roleID = roleID }
    }
}