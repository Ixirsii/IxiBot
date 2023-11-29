package com.ixibot.listener

import arrow.core.Option
import com.google.common.eventbus.EventBus
import com.ixibot.event.DiscordReactionEvent
import com.ixibot.extensions.toOption
import com.ixibot.logging.Logging
import com.ixibot.logging.LoggingImpl
import discord4j.common.util.Snowflake
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.ReactionAddEvent
import discord4j.core.event.domain.message.ReactionRemoveEvent

/**
 * Listen for events from Discord.
 *
 * @author Ixirsii <ixirsii@ixirsii.tech>
 */
class DiscordListener(
    /** Event bus to publish events to. */
    private val eventBus: EventBus,
) : Logging by LoggingImpl<DiscordListener>() {

    /**
     * ReactionAddEvent listener.
     *
     * @param event Event to handle.
     */
    fun reactionAddListener(event: ReactionAddEvent) {
        val guildID: Option<Snowflake> = event.guildId.toOption()

        val name: String = if (event.emoji.asCustomEmoji().isPresent) {
            event.emoji.asCustomEmoji().get().name
        } else {
            event.emoji.asUnicodeEmoji().get().raw
        }

        val internalEvent = DiscordReactionEvent(
            channelID = event.channelId,
            isAdd = true,
            guildID = guildID,
            messageMono = event.message,
            messageID = event.messageId,
            name = name,
            userID = event.userId
        )

        eventBus.post(internalEvent)
    }

    /**
     * ReactionRemoveEvent listener.
     *
     * @param event Event to handle.
     */
    fun reactionRemoveListener(event: ReactionRemoveEvent) {
        val guildID: Option<Snowflake> = event.guildId.toOption()

        val name: String = if (event.emoji.asCustomEmoji().isPresent) {
            event.emoji.asCustomEmoji().get().name
        } else {
            event.emoji.asUnicodeEmoji().get().raw
        }

        val internalEvent = DiscordReactionEvent(
            channelID = event.channelId,
            isAdd = false,
            guildID = guildID,
            messageMono = event.message,
            messageID = event.messageId,
            name = name,
            userID = event.userId
        )

        eventBus.post(internalEvent)
    }

    fun readyListener(event: ReadyEvent) {
        log.info("Connected to Discord {}", event)
    }
}
