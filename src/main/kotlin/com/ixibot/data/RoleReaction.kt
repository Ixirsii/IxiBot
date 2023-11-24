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
    /** Reaction emoji name. */
    val name: String,
    /** Role ID to (un)assign. */
    val roleID: Snowflake
)
