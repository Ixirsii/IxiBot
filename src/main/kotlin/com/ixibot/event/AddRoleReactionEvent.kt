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

package com.ixibot.event

import discord4j.core.`object`.reaction.ReactionEmoji
import discord4j.core.`object`.util.Snowflake

/**
 * Create role reaction pub/sub event.
 */
class AddRoleReactionEvent(
    // Options
    /** Was the help flag passed? */
    isHelp: Boolean,
    /** Was the call to the command valid? */
    isValid: Boolean,
    /** Was the verify flag passed? */
    val isVerify: Boolean,
    /** Was the verify_add flag passed? */
    val isVerifyAdd: Boolean,
    /** Was the verify_remove flag passed? */
    val isVerifyRemove: Boolean,
    /** Channel ID containing the message. */
    val channelID: Snowflake?,
    /** Message ID containing the reaction. */
    val messageID: Snowflake?,
    /** Reaction emoji name/raw. */
    val reactionEmoji: ReactionEmoji?,
    /** Role ID to (un)assign. */
    val roleID: Snowflake?
) : CommandEvent(isHelp = isHelp, isValid = isValid)

/**
 * Builder class for AddRoleReactionEvent.
 */
class AddRoleReactionEventBuilder: Builder<AddRoleReactionEvent, AddRoleReactionEventBuilder>() {
    private var channelID: Snowflake? = null
    private var isVerify: Boolean = false
    private var isVerifyAdd: Boolean = false
    private var isVerifyRemove: Boolean = false
    private var messageID: Snowflake? = null
    private var reactionEmoji: ReactionEmoji? = null
    private var roleID: Snowflake? = null

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

    override fun getThis(): AddRoleReactionEventBuilder {
        return this
    }

    fun channelID(channelID: Snowflake): AddRoleReactionEventBuilder {
        this.channelID = channelID

        return this
    }

    fun isVerify(verify: Boolean): AddRoleReactionEventBuilder {
        this.isVerify = verify

        return this
    }

    fun isVerifyAdd(verifyAdd: Boolean): AddRoleReactionEventBuilder {
        this.isVerifyAdd = verifyAdd

        return this
    }

    fun isVerifyRemove(verifyRemove: Boolean): AddRoleReactionEventBuilder {
        this.isVerifyRemove = verifyRemove

        return this
    }

    fun messageID(messageID: Snowflake): AddRoleReactionEventBuilder {
        this.messageID = messageID

        return this
    }

    fun reactionEmoji(reactionEmoji: ReactionEmoji): AddRoleReactionEventBuilder {
        this.reactionEmoji = reactionEmoji

        return this
    }

    fun roleID(roleID: Snowflake): AddRoleReactionEventBuilder {
        this.roleID = roleID

        return this
    }
}
