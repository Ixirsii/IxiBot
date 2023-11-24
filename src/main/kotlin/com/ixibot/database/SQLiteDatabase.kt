package com.ixibot.database

import arrow.core.*
import com.ixibot.data.RoleReaction
import com.ixibot.exception.DatabaseException
import com.ixibot.logging.Logging
import com.ixibot.logging.LoggingImpl
import discord4j.common.util.Snowflake
import org.koin.core.annotation.Single
import java.sql.Connection
import java.sql.SQLException
import javax.xml.crypto.Data

/**
 * Database version number.
 */
private const val DATABASE_VERSION: Long = 1

/**
 * SQLite database interface.
 *
 * @author Ixirsii <ixirsii@ixirsii.tech>
 */
@Single
class SQLiteDatabase(
    /** Connection to SQLite database. */
    private val connection: Connection,
) : AutoCloseable, Database, Logging by LoggingImpl<SQLiteDatabase>() {

    /**
     * Get all role reactions from database.
     *
     * @return List of all role reactions.
     */
    override val roleReactions: Either<DatabaseException, List<RoleReaction>>
        get() {
            log.trace("Getting all role assignment reactions")

            return Either.catch {
                val roleReactions: MutableList<RoleReaction> = ArrayList()

                connection.prepareStatement("SELECT * FROM $TABLE_NAME").use { statement ->
                    statement.executeQuery().use { resultSet ->
                        while (resultSet.next()) {
                            val roleReaction = RoleReaction(
                                channelID = Snowflake.of(resultSet.getLong(CHANNEL_ID)),
                                guildID = Snowflake.of(resultSet.getLong(GUILD_ID)),
                                messageID = Snowflake.of(resultSet.getLong(MESSAGE_ID)),
                                name = resultSet.getString(NAME),
                                roleID = Snowflake.of(resultSet.getLong(ROLE_ID))
                            )
                            roleReactions.add(roleReaction)
                        }
                    }
                }

                return roleReactions.right()
            }
                .mapLeft { DatabaseException.SQLException(it.message ?: it.stackTraceToString(), it) }
        }

    /**
     * Insert a role assignment reaction into the database.
     *
     * @param guildID
     * @param channelID
     * @param messageID
     * @param name
     * @param roleID
     * @return `true` if the first result is a `ResultSet` object;
     * `false` if the first result is an update count or there is no result.
     */
    override fun addRoleReaction(
        guildID: Snowflake,
        channelID: Snowflake,
        messageID: Snowflake,
        name: String,
        roleID: Snowflake,
    ): Either<DatabaseException, Boolean> {
        log.trace("Adding role reaction to database: {}", name)

        return Either.catch {
            connection.prepareStatement(
                "INSERT INTO $TABLE_NAME($CHANNEL_ID, $GUILD_ID, $MESSAGE_ID, $NAME, $ROLE_ID) VALUES (?, ?, ?, ?, ?)"
            ).use { statement ->
                statement.setString(1, channelID.asString())
                statement.setString(2, guildID.asString())
                statement.setString(3, messageID.asString())
                statement.setString(4, name)
                statement.setString(5, roleID.asString())

                return statement.execute().right()
            }
        }
            .mapLeft { DatabaseException.SQLException(it.message ?: it.stackTraceToString(), it) }
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
     * Get/read a role reaction from the database.
     *
     * @param messageID Message ID.
     * @param name Reaction name
     */
    override fun getRoleReaction(messageID: Snowflake, name: String): Either<DatabaseException, Option<RoleReaction>> {
        log.trace("Getting reaction {} on message {}", name, messageID)

        return Either.catch {
            connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE $MESSAGE_ID = ? AND $NAME = ?")
                .use { statement ->
                    statement.executeQuery().use { resultSet ->
                        return if (resultSet.next()) {
                            RoleReaction(
                                channelID = Snowflake.of(resultSet.getLong(CHANNEL_ID)),
                                guildID = Snowflake.of(resultSet.getLong(GUILD_ID)),
                                messageID = Snowflake.of(resultSet.getLong(MESSAGE_ID)),
                                name = resultSet.getString(NAME),
                                roleID = Snowflake.of(resultSet.getLong(ROLE_ID))
                            ).some().right()
                        } else {
                            none<RoleReaction>().right()
                        }
                    }
                }
        }
            .mapLeft { DatabaseException.SQLException(it.message ?: it.stackTraceToString(), it) }
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
     * Delete a role assignment reaction from the database.
     *
     * @param messageID
     * @param name
     * @return `true` if the first result is a `ResultSet` object;
     * `false` if the first result is an update count or there is no result.
     */
    override fun removeRoleReaction(messageID: Snowflake, name: String): Either<DatabaseException, Boolean> {
        log.trace("Deleting role reaction from database: {}", name)

        return Either.catch {
            connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE $MESSAGE_ID = ? AND $NAME = ?")
                .use { statement ->
                    statement.setLong(1, messageID.asLong())
                    statement.setString(2, name)

                    return statement.execute().right()
                }
        }
            .mapLeft { DatabaseException.SQLException(it.message ?: it.stackTraceToString(), it) }
    }

    /* **************************************** Private utility methods ***************************************** */

    /**
     * Create role assignment reactions table.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Throws(SQLException::class)
    private fun createTable() {
        log.trace("Creating role assignment reactions table")
        connection.prepareStatement(CREATE_TABLE).use { createStatement -> createStatement.execute() }
        connection.prepareStatement("PRAGMA user_version = ?").use { versionStatement ->
            versionStatement.setLong(1, DATABASE_VERSION)
            versionStatement.execute()
        }
    }

    /**
     * Drop role assignment reactions table.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Throws(SQLException::class)
    private fun dropTable() {
        log.trace("Dropping role assignment reactions table")

        connection.prepareStatement("DROP TABLE IF EXISTS $TABLE_NAME").use { statement -> statement.execute() }
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