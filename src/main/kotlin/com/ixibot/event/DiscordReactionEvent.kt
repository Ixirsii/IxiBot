package com.ixibot.event

import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.reaction.ReactionEmoji
import reactor.core.publisher.Mono

/**
 * Discord reaction event for pub/sub.
 *
 * @author Ixirsii <ixirsii@ixirsii.tech>
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
    val userID: Snowflake,
)
