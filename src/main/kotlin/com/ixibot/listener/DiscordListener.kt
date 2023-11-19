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
 * @author Ixirsii <ixirsii@ixirsii.tech>
 */
class DiscordListener(
    /** Discord Gateway event dispatcher. */
    eventDispatcher: EventDispatcher,
    /** Event bus to publish events to. */
    private val eventBus: EventBus,
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
