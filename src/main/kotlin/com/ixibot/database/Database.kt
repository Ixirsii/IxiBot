/*
 * Copyright (c) 2019, Ryan Porterfield
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

package com.ixibot.database

import com.ixibot.Logging
import com.ixibot.LoggingImpl
import com.ixibot.data.RoleReaction
import discord4j.core.`object`.reaction.ReactionEmoji
import discord4j.core.`object`.util.Snowflake
import java.sql.Connection
import java.sql.SQLException

/**
 * Database version number.
 */
private const val DATABASE_VERSION: Long = 1

/**
 * SQLite database connector.
 *
 * @author Ryan Porterfield
 */
class Database(
        /**
         * Connection to SQLite database.
         */
        private val connection: Connection
) : Logging by LoggingImpl<Database>() {

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
        val insertStatement = String.format("INSERT INTO %s(%s, %s, %s, %s, %s, %s, %s, %s)"
                + " VALUES (%s, %s, %s, %s, %d, %s, %s, %s)",
                RoleReactionContract.TABLE_NAME,
                RoleReactionContract.ADD_VERIFIED,
                RoleReactionContract.CHANNEL_ID,
                RoleReactionContract.GUILD_ID,
                RoleReactionContract.MESSAGE_ID,
                RoleReactionContract.REACTION_ID,
                RoleReactionContract.REACTION_NAME,
                RoleReactionContract.REMOVE_VERIFIED,
                RoleReactionContract.ROLE_ID,
                roleReaction.isAddVerified,
                roleReaction.channelID,
                roleReaction.guildID,
                roleReaction.messageID,
                roleReaction.boxedReactionEmojiID,
                roleReaction.reactionEmojiName,
                roleReaction.isRemoveVerified,
                roleReaction.roleID)

        connection.prepareStatement(insertStatement).use { statement -> return statement.execute() }
    }

    /**
     * Close database connection.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Throws(SQLException::class)
    fun close() {
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
                RoleReactionContract.CREATE_TABLE).use { createStatement -> createStatement.execute() }
        connection.prepareStatement(String.format("PRAGMA user_version = %d", DATABASE_VERSION)).use { versionStatement -> versionStatement.execute() }
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
        val deleteStatement = String.format("DELETE FROM %s WHERE %s = %d AND %s = %s",
                RoleReactionContract.TABLE_NAME,
                RoleReactionContract.MESSAGE_ID,
                reaction.messageID.asLong(),
                RoleReactionContract.REACTION_NAME,
                reaction.reactionEmojiName)
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
        val dropStatement = String.format("DROP TABLE IF EXISTS %s",
                RoleReactionContract.TABLE_NAME)
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
            val selectStatement = String.format("SELECT * FROM %s",
                    RoleReactionContract.TABLE_NAME)
            connection.prepareStatement(selectStatement).use { statement ->
                statement.executeQuery().use { resultSet ->
                    while (resultSet.next()) {
                        val animated = resultSet.getBoolean(RoleReactionContract.ANIMATED)
                        val reactionId = resultSet.getLong(RoleReactionContract.REACTION_ID)
                        val reactionName = resultSet.getString(RoleReactionContract.REACTION_NAME)
                        val reactionEmoji: ReactionEmoji = ReactionEmoji.of(
                                reactionId,
                                reactionName,
                                animated)
                        val roleReaction = RoleReaction(
                                Snowflake.of(resultSet.getLong(RoleReactionContract.CHANNEL_ID)),
                                Snowflake.of(resultSet.getLong(RoleReactionContract.GUILD_ID)),
                                resultSet.getBoolean(RoleReactionContract.ADD_VERIFIED),
                                resultSet.getBoolean(RoleReactionContract.REMOVE_VERIFIED),
                                Snowflake.of(resultSet.getLong(RoleReactionContract.MESSAGE_ID)),
                                reactionEmoji,
                                Snowflake.of(resultSet.getLong(RoleReactionContract.ROLE_ID)))
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