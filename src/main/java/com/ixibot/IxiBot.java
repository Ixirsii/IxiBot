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

package com.ixibot;

import com.ixibot.api.DiscordAPI;
import com.ixibot.data.BotConfiguration;
import com.ixibot.data.RoleReaction;
import com.ixibot.database.Database;

import java.sql.SQLException;
import java.util.List;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Bot logic class once startup is complete and user configuration is loaded.
 *
 * @author Ryan Porterfield
 */
@Slf4j
public class IxiBot implements AutoCloseable, Runnable {
    /**
     * Bot configuration.
     */
    @NonNull
    private final BotConfiguration botConfiguration;
    /**
     * Discord API interface.
     */
    @NonNull
    private final DiscordAPI discordAPI;
    /**
     * Database interface.
     */
    @NonNull
    private final Database database;

    /**
     * Constructor.
     *
     * @param botConfiguration Bot configuration parsed from user config file.
     * @throws ClassNotFoundException on failure to load JDBC driver.
     * @throws SQLException if a database access error occurs.
     */
    IxiBot(@NonNull final BotConfiguration botConfiguration)
            throws ClassNotFoundException, SQLException {
        this.botConfiguration = botConfiguration;
        this.database = new Database();
        this.discordAPI = new DiscordAPI(botConfiguration.getDiscordToken(),
                database.getAllRoleReactions());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        log.trace("Shutting down bot");

        try {
            database.close();
        } catch (final SQLException sqle) {
            log.error("Caught SQLException while attempting to close database", sqle);
        }

        discordAPI.logout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if (discordAPI.getRoleReactions().isEmpty()) {
            final RoleReaction roleReaction0 = new RoleReaction(0, 0, 0, 0, 0);
            final RoleReaction roleReaction1 = new RoleReaction(1, 1, 1, 1, 1);

            database.addRoleReaction(roleReaction0);
            database.addRoleReaction(roleReaction1);
            discordAPI.addRoleReaction(roleReaction0);
            discordAPI.addRoleReaction(roleReaction1);
        }

        try {
            final List<RoleReaction> roleReactions = database.getAllRoleReactions();
            log.info("Got role reactions from database {}", roleReactions);
        } catch (final SQLException sqle) {
            log.error("Caught SQLException attempting to get roll reactions from database", sqle);
        }
    }
}
