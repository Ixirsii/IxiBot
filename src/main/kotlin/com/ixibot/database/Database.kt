package com.ixibot.database

import com.ixibot.Logging
import com.ixibot.LoggingImpl
import com.ixibot.data.RoleReaction
import discord4j.common.util.Snowflake
import discord4j.core.`object`.reaction.ReactionEmoji
import java.sql.Connection
import java.sql.SQLException

/**
 * Database version number.
 */
private const val DATABASE_VERSION: Long = 1

/**
 * SQLite database connector.
 *
 * @author Ixirsii <ixirsii@ixirsii.tech>
 */
class Database(
    /** Connection to SQLite database. */
    private val connection: Connection,
) : AutoCloseable, Logging by LoggingImpl<Database>() {

    /**
     * Insert a role assignment reaction into the database.
     *
     * @param roleReaction Role assignment reaction to persist to the database.
     * @return `true` if the first result is a `ResultSet` object;
     * `false` if the first result is an update count or there is no result.
     * @throws SQLException if a database access error occurs.
     */
    @Throws(SQLException::class)
    fun addRoleReaction(roleReaction: RoleReaction): Boolean {
        log.trace("Adding role reaction to database: {}", roleReaction)
        val insertStatement = String.format(
            "INSERT INTO %s(%s, %s, %s, %s, %s, %s, %s, %s)" +
                    " VALUES (%s, %s, %s, %s, %d, %s, %s, %s)",
            TABLE_NAME,
            ADD_VERIFIED,
            CHANNEL_ID,
            GUILD_ID,
            MESSAGE_ID,
            REACTION_ID,
            REACTION_NAME,
            REMOVE_VERIFIED,
            ROLE_ID,
            roleReaction.isAddVerified,
            roleReaction.channelID,
            roleReaction.guildID,
            roleReaction.messageID,
            roleReaction.boxedReactionEmojiID,
            roleReaction.reactionEmojiName,
            roleReaction.isRemoveVerified,
            roleReaction.roleID
        )

        connection.prepareStatement(insertStatement).use { statement -> return statement.execute() }
    }

    /**
     * Close database connection.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Throws(SQLException::class)
    override fun close() {
        log.trace("Closing database connection")
        connection.close()
    }

    /**
     * Create role assignment reactions table.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Throws(SQLException::class)
    private fun createTable() {
        log.trace("Creating role assignment reactions table")
        connection.prepareStatement(
            CREATE_TABLE
        ).use { createStatement -> createStatement.execute() }
        connection.prepareStatement(String.format("PRAGMA user_version = %d", DATABASE_VERSION))
            .use { versionStatement -> versionStatement.execute() }
    }

    /**
     * Delete a role assignment reaction from the database.
     *
     * @param reaction Role assignment reaction to delete from the database.
     * @return `true` if the first result is a `ResultSet` object;
     * `false` if the first result is an update count or there is no result.
     * @throws SQLException if a database access error occurs.
     */
    @Throws(SQLException::class)
    fun deleteRoleReaction(reaction: RoleReaction): Boolean {
        log.trace("Deleting role reaction from database: {}", reaction)
        val deleteStatement = String.format(
            "DELETE FROM %s WHERE %s = %d AND %s = %s",
            TABLE_NAME,
            MESSAGE_ID,
            reaction.messageID.asLong(),
            REACTION_NAME,
            reaction.reactionEmojiName
        )
        connection.prepareStatement(deleteStatement).use { statement -> return statement.execute() }
    }

    /**
     * Drop role assignment reactions table.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Throws(SQLException::class)
    private fun dropTable() {
        log.trace("Dropping role assignment reactions table")
        val dropStatement = String.format(
            "DROP TABLE IF EXISTS %s",
            TABLE_NAME
        )
        connection.prepareStatement(dropStatement).use { statement -> statement.execute() }
    }

    /**
     * Get all role reactions from database.
     *
     * @return List of all role reactions.
     * @throws SQLException if a database access error occurs.
     */
    @get:Throws(SQLException::class)
    val allRoleReactions: List<RoleReaction>
        get() {
            log.trace("Getting all role assignment reactions")
            val roleReactions: MutableList<RoleReaction> = ArrayList()
            val selectStatement = String.format(
                "SELECT * FROM %s",
                TABLE_NAME
            )
            connection.prepareStatement(selectStatement).use { statement ->
                statement.executeQuery().use { resultSet ->
                    while (resultSet.next()) {
                        val animated = resultSet.getBoolean(ANIMATED)
                        val reactionId = resultSet.getLong(REACTION_ID)
                        val reactionName = resultSet.getString(REACTION_NAME)
                        val reactionEmoji: ReactionEmoji = ReactionEmoji.of(
                            reactionId,
                            reactionName,
                            animated
                        )
                        val roleReaction = RoleReaction(
                            Snowflake.of(resultSet.getLong(CHANNEL_ID)),
                            Snowflake.of(resultSet.getLong(GUILD_ID)),
                            resultSet.getBoolean(ADD_VERIFIED),
                            resultSet.getBoolean(REMOVE_VERIFIED),
                            Snowflake.of(resultSet.getLong(MESSAGE_ID)),
                            reactionEmoji,
                            Snowflake.of(resultSet.getLong(ROLE_ID))
                        )
                        roleReactions.add(roleReaction)
                    }
                }
            }
            return roleReactions
        }

    /**
     * Update the database if the existing database version is out of date.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Throws(SQLException::class)
    fun init() {
        createTable()
        log.trace("Getting database version")
        connection.createStatement().use { statement ->
            statement.executeQuery("PRAGMA user_version").use { versionResult ->
                val databaseVersion: Long = if (versionResult.next()) {
                    versionResult.getLong(1)
                } else {
                    0
                }

                if (databaseVersion < DATABASE_VERSION) {
                    updateDatabase()
                }
            }
        }
    }

    /**
     * Update database version.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Throws(SQLException::class)
    private fun updateDatabase() {
        dropTable()
        createTable()
    }
}
