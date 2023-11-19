package com.ixibot.data

import discord4j.common.util.Snowflake
import discord4j.core.`object`.reaction.ReactionEmoji
import java.util.Optional

/**
 * Role assignment reaction POJO.
 *
 * @author Ixirsii <ixirsii@ixirsii.tech>
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
