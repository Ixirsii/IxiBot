package com.ixibot.database

import arrow.core.Either
import arrow.core.Option
import com.ixibot.data.RoleReaction
import com.ixibot.exception.DatabaseException
import discord4j.common.util.Snowflake
import java.util.Optional

/**
 * Database interface.
 *
 * @author Ixirsii <ixirsii@ixirsii.tech>
 */
interface Database : AutoCloseable {
    /**
     * List of all role reactions.
     */
    val roleReactions: Either<DatabaseException, List<RoleReaction>>

    /**
     * Add/create a role reaction.
     */
    fun addRoleReaction(
        guildID: Snowflake,
        channelID: Snowflake,
        messageID: Snowflake,
        name: String,
        roleID: Snowflake,
    ): Either<DatabaseException, Boolean>

    /**
     * Get role reaction.
     */
    fun getRoleReaction(messageID: Snowflake, name: String): Either<DatabaseException, Option<RoleReaction>>

    /**
     * Remove/delete a role reaction.
     */
    fun removeRoleReaction(messageID: Snowflake, name: String): Either<DatabaseException, Boolean>
}
