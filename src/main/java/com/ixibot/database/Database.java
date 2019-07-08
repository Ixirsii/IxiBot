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
import java.util.ArrayList;
import java.util.List;

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
    private static final String DATABASE_FILE_PATH = "jdbc:sqlite:sqlite.db";

    /**
     * Connection to SQLite database.
     */
    @NonNull
    private final Connection connection;

    /**
     * Constructor.
     *
     * @throws ClassNotFoundException on failure to load JDBC driver.
     * @throws SQLException if a database access error occurs.
     */
    public Database() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");

        this.connection = DriverManager.getConnection(DATABASE_FILE_PATH);
        createTable();
    }

    /**
     * Insert a role assignment reaction into the database.
     *
     * @param roleReaction Role assignment reaction to persist to the database.
     * @return <code>true</code> if the first result is a <code>ResultSet</code>
     *         object; <code>false</code> if the first result is an update
     *         count or there is no result.
     */
    public boolean addRoleReaction(@NonNull final RoleReaction roleReaction) {
        log.trace("Adding role reaction to database: {}", roleReaction);

        try {
            final PreparedStatement insertStatement = connection.prepareStatement(
                    String.format(
                            "INSERT INTO %s (%s, %s, %s, %s, %s) VALUES(%s, %s, %s, %s, %s)",
                            RoleReactionContract.TABLE_NAME,
                            /* Columns */
                            RoleReactionContract.MESSAGE_ID,
                            RoleReactionContract.REACTION_ID,
                            RoleReactionContract.CHANNEL_ID,
                            RoleReactionContract.GUILD_ID,
                            RoleReactionContract.ROLE_ID,
                            /* Values */
                            roleReaction.getMessageId(),
                            roleReaction.getReactionId(),
                            roleReaction.getChannelId(),
                            roleReaction.getGuildId(),
                            roleReaction.getRoleId()));

            return insertStatement.execute();
        } catch (final SQLException sqle) {
            log.error("Caught exception trying to insert reaction {} into database",
                    roleReaction,
                    sqle);
            return false;
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
    public void createTable() throws SQLException {
        log.trace("Creating role assignment reactions table");

        final PreparedStatement createStatement = connection.prepareStatement(
                RoleReactionContract.CREATE_TABLE_STATEMENT);

        createStatement.execute();
    }

    /**
     * Drop role assignment reactions table.
     *
     * @throws SQLException if a database access error occurs.
     */
    public void dropTable() throws SQLException {
        log.trace("Dropping role assignment reactions table");

        final PreparedStatement dropStatement = connection.prepareStatement(
                String.format("DROP TABLE IF EXISTS %s", RoleReactionContract.TABLE_NAME));

        dropStatement.execute();
    }

    /**
     * Get all role reactions from database.
     *
     * @return List of all role reactions.
     * @throws SQLException  if a database access error occurs.
     */
    public List<RoleReaction> getAllRoleReactions() throws SQLException {
        log.trace("Getting all role assignment reactions");

        final List<RoleReaction> roleReactions = new ArrayList<>();
        final PreparedStatement selectStatement = connection.prepareStatement(
                String.format("SELECT * FROM %s", RoleReactionContract.TABLE_NAME));

        final ResultSet resultSet = selectStatement.executeQuery();
        while (resultSet.next()) {
            final long channelId = resultSet.getLong(RoleReactionContract.CHANNEL_ID);
            final long guildId = resultSet.getLong(RoleReactionContract.GUILD_ID);
            final long messageId = resultSet.getLong(RoleReactionContract.MESSAGE_ID);
            final long reactionId = resultSet.getLong(RoleReactionContract.REACTION_ID);
            final long roleId = resultSet.getLong(RoleReactionContract.ROLE_ID);

            final RoleReaction roleReaction = new RoleReaction(
                    channelId, guildId, messageId, reactionId, roleId);
            roleReactions.add(roleReaction);
        }

        return roleReactions;
    }
}
