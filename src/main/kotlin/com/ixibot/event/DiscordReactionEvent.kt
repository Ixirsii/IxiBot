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

import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.reaction.ReactionEmoji
import discord4j.core.`object`.util.Snowflake
import reactor.core.publisher.Mono

/**
 * Discord reaction event for pub/sub.
 *
 * @author Ryan Porterfield
 */
data class DiscordReactionEvent(
        /**
         * ID of the message that was reacted to.
         */
        val channelID: Snowflake,
        /**
         * `true` if this is a reaction add event, otherwise this is a reaction remove event.
         */
        val isAdd: Boolean,
        /**
         * Mono that can be subscribed to to get the message that was reacted to.
         */
        val messageMono: Mono<Message>,
        /**
         * ID of the message that was reacted to.
         */
        val messageID: Snowflake,
        /**
         * Emoji the user reacted with.
         */
        val reactionEmoji: ReactionEmoji,
        /**
         * ID of the user who reacted on the message.
         */
        val userID: Snowflake
)