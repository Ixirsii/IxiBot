package com.ixibot.data

import discord4j.common.util.Snowflake
import discord4j.core.`object`.reaction.ReactionEmoji
import java.util.Optional

/**
 * Role assignment reaction POJO.
 *
 * @author Ixirsii <ixirsii@ixirsii.tech>
 */
data class RoleReaction(
    /** Channel ID containing the message. */
    val channelID: Snowflake,
    /** Guild ID containing the channel. */
    val guildID: Snowflake,
    /** Message ID containing the reaction. */
    val messageID: Snowflake,
    /** Reaction emoji name/raw. */
    val reactionEmoji: ReactionEmoji,
    /** Role ID to (un)assign. */
    val roleID: Snowflake
) {

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
            return optionalCustom.map { it.name }
                .orElseGet { optionalUnicode.map { it.raw }.orElse("") }
        }
}
