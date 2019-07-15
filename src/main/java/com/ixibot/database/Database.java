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

package com.ixibot.database;

import com.ixibot.data.RoleReaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.object.util.Snowflake;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * SQLite database connector.
 *
 * @author Ryan Porterfield
 */
@Slf4j
public class Database {
    /**
     * URL/path to SQLite database file.
     */
    private static final String CONNECTION_URL = "jdbc:sqlite:sqlite.db";
    /**
     * Database version number.
     */
    private static final long DATABASE_VERSION = 1;

    /**
     * Connection to SQLite database.
     */
    @NonNull
    private final Connection connection;

    /**
     * Constructor.
     *
     * @throws ClassNotFoundException on failure to load JDBC driver.
     * @throws SQLException           if a database access error occurs.
     */
    public Database() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");

        this.connection = DriverManager.getConnection(CONNECTION_URL);

        try (Statement statement = connection.createStatement();
                ResultSet versionResult = statement.executeQuery("PRAGMA schema.user_version")) {
            final long databaseVersion;

            if (versionResult.next()) {
                databaseVersion = versionResult.getLong(1);
            } else {
                databaseVersion = 0;
            }

            if (databaseVersion < DATABASE_VERSION) {
                updateDatabase();
            }
        }
    }

    /**
     * Insert a role assignment reaction into the database.
     *
     * @param roleReaction Role assignment reaction to persist to the database.
     * @return {@code true} if the first result is a {@code ResultSet} object;
     *      {@code false} if the first result is an update count or there is no result.
     * @throws SQLException if a database access error occurs.
     */
    public boolean addRoleReaction(@NonNull final RoleReaction roleReaction) throws SQLException {
        log.trace("Adding role reaction to database: {}", roleReaction);

        final String insertStatement = String.format(
                "INSERT INTO %s(%s, %s, %s, %s, %s, %s, %s, %s)"
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
                roleReaction.isAddVerified(),
                roleReaction.getChannelID(),
                roleReaction.getGuildID(),
                roleReaction.getMessageID(),
                roleReaction.getBoxedReactionEmojiID(),
                roleReaction.getReactionEmojiName(),
                roleReaction.isRemoveVerified(),
                roleReaction.getRoleID());

        try (PreparedStatement statement = connection.prepareStatement(insertStatement)) {
            return statement.execute();
        }
    }

    /**
     * Close database connection.
     *
     * @throws SQLException if a database access error occurs.
     */
    public void close() throws SQLException {
        log.trace("Closing database connection");

        connection.close();
    }

    /**
     * Create role assignment reactions table.
     *
     * @throws SQLException if a database access error occurs.
     */
    private void createTable() throws SQLException {
        log.trace("Creating role assignment reactions table");

        try (PreparedStatement createStatement = connection.prepareStatement(
                RoleReactionContract.CREATE_TABLE)) {
            createStatement.execute();
        }
    }

    /**
     * Delete a role assignment reaction from the database.
     *
     * @param reaction Role assignment reaction to delete from the database.
     * @return {@code true} if the first result is a {@code ResultSet} object;
     *      {@code false} if the first result is an update count or there is no result.
     * @throws SQLException if a database access error occurs.
     */
    public boolean deleteRoleReaction(@NonNull final RoleReaction reaction) throws SQLException {
        log.trace("Deleting role reaction from database: {}", reaction);

        final String deleteStatement = String.format("DELETE FROM %s WHERE %s = %d AND %s = %s",
                RoleReactionContract.TABLE_NAME,
                RoleReactionContract.MESSAGE_ID,
                reaction.getMessageID().asLong(),
                RoleReactionContract.REACTION_NAME,
                reaction.getReactionEmojiName());

        try (PreparedStatement statement = connection.prepareStatement(deleteStatement)) {
            return statement.execute();
        }
    }

    /**
     * Drop role assignment reactions table.
     *
     * @throws SQLException if a database access error occurs.
     */
    private void dropTable() throws SQLException {
        log.trace("Dropping role assignment reactions table");

        final String dropStatement = String.format("DROP TABLE IF EXISTS %s",
                RoleReactionContract.TABLE_NAME);

        try (PreparedStatement statement = connection.prepareStatement(dropStatement)) {
            statement.execute();
        }
    }

    /**
     * Get all role reactions from database.
     *
     * @return List of all role reactions.
     * @throws SQLException if a database access error occurs.
     */
    public List<RoleReaction> getAllRoleReactions() throws SQLException {
        log.trace("Getting all role assignment reactions");

        final List<RoleReaction> roleReactions = new ArrayList<>();
        final String selectStatement = String.format("SELECT * FROM %s",
                RoleReactionContract.TABLE_NAME);

        try (PreparedStatement statement = connection.prepareStatement(selectStatement);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                final boolean animated = resultSet.getBoolean(RoleReactionContract.ANIMATED);
                final long reactionId = resultSet.getLong(RoleReactionContract.REACTION_ID);
                final String reactionName = resultSet.getString(RoleReactionContract.REACTION_NAME);
                final ReactionEmoji reactionEmoji = ReactionEmoji.of(
                        reactionId,
                        reactionName,
                        animated);
                @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
                final RoleReaction roleReaction = new RoleReaction(
                        resultSet.getBoolean(RoleReactionContract.ADD_VERIFIED),
                        Snowflake.of(resultSet.getLong(RoleReactionContract.CHANNEL_ID)),
                        Snowflake.of(resultSet.getLong(RoleReactionContract.GUILD_ID)),
                        Snowflake.of(resultSet.getLong(RoleReactionContract.MESSAGE_ID)),
                        reactionEmoji,
                        resultSet.getBoolean(RoleReactionContract.REMOVE_VERIFIED),
                        Snowflake.of(resultSet.getLong(RoleReactionContract.ROLE_ID)));

                roleReactions.add(roleReaction);
            }
        }

        return roleReactions;
    }

    /**
     * Update database version.
     *
     * @throws SQLException if a database access error occurs.
     */
    private void updateDatabase() throws SQLException {
        dropTable();
        createTable();
    }
}
