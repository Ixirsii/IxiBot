/*
 * Copyright (c) 2020, Ryan Porterfield
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

package com.ixibot.listener

import com.google.common.eventbus.EventBus
import com.ixibot.Logging
import com.ixibot.LoggingImpl
import com.ixibot.event.DiscordReactionEvent
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.entity.Member
import discord4j.core.event.EventDispatcher
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.event.domain.message.ReactionAddEvent
import discord4j.core.event.domain.message.ReactionRemoveEvent

/**
 * Listen for events from Discord.
 *
 * @author Ryan Porterfield
 */
class DiscordListener(
    /** Discord Gateway event dispatcher. */
    eventDispatcher: EventDispatcher,
    /** Event bus to publish events to. */
    private val eventBus: EventBus
) : Logging by LoggingImpl<DiscordListener>() {

    init {
        eventDispatcher.on(MessageCreateEvent::class.java)
            .subscribe(::messageCreateListener)
        eventDispatcher.on(ReactionAddEvent::class.java)
            .subscribe(::reactionAddListener)
        eventDispatcher.on(ReactionRemoveEvent::class.java)
            .subscribe(::reactionRemoveListener)
    }

    /**
     * MessageCreateEvent listener.
     *
     * @param event Event to handle.
     */
    private fun messageCreateListener(event: MessageCreateEvent) {
        val optionalMember = event.member
        val message = event.message
        log.info(
            "#{} [{}]: {}",
            message.channelId.asLong(),
            optionalMember.map { member: Member -> member.displayName }.orElse(""),
            message.content
        )
    }

    /**
     * ReactionAddEvent listener.
     *
     * @param event Event to handle.
     */
    private fun reactionAddListener(event: ReactionAddEvent) {
        val internalEvent = DiscordReactionEvent(
            channelID = event.channelId,
            isAdd = true,
            messageMono = event.message,
            messageID = event.messageId,
            reactionEmoji = event.emoji,
            userID = event.userId
        )

        eventBus.post(internalEvent)
    }

    /**
     * ReactionRemoveEvent listener.
     *
     * @param event Event to handle.
     */
    private fun reactionRemoveListener(event: ReactionRemoveEvent) {
        val internalEvent = DiscordReactionEvent(
            channelID = event.channelId,
            isAdd = false,
            messageMono = event.message,
            messageID = event.messageId,
            reactionEmoji = event.emoji,
            userID = event.userId
        )

        eventBus.post(internalEvent)
    }
}
