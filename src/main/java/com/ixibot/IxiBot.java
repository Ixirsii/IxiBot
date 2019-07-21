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
import com.ixibot.data.RoleReaction;
import com.ixibot.database.Database;
import com.ixibot.event.RoleReactionEvent;

import java.sql.SQLException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.eventbus.Subscribe;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Bot logic class once startup is complete and user configuration is loaded.
 *
 * @author Ryan Porterfield
 */
@RequiredArgsConstructor
@Slf4j
public class IxiBot implements AutoCloseable, Runnable {
    /**
     * Database interface.
     */
    @NonNull
    private final Database database;
    /**
     * Discord API interface.
     */
    @NonNull
    private final DiscordAPI discordAPI;
    /**
     * Interval (in minutes) between Discord role verification checks.
     */
    private final long roleVerifyDelay;
    /**
     * Thread pool executor for scheduled async actions.
     */
    @NonNull
    private final ScheduledThreadPoolExecutor scheduler;

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        log.trace("Shutting down bot");

        scheduler.shutdown();

        try {
            database.close();
        } catch (final SQLException sqle) {
            log.error("Caught SQLException while attempting to close database", sqle);
        }

        discordAPI.logout();
    }

    /**
     * RoleReactionEvent subscriber.
     *
     * @param event Event published to event bus.
     */
    @Subscribe
    public void onRoleReactionEvent(@NonNull final RoleReactionEvent event) {
        final RoleReaction roleReaction = event.getRoleReaction();

        try {
            if (event.isCreate()) {
                database.addRoleReaction(roleReaction);
                discordAPI.addRoleReaction(roleReaction);
            } else {
                database.deleteRoleReaction(roleReaction);
                discordAPI.removeRoleReaction(roleReaction);
            }
        } catch (final SQLException sqle) {
            log.error("Failed to process role reaction event {}", event, sqle);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        scheduler.scheduleAtFixedRate(
                discordAPI::updateAllRoles,
                0,
                roleVerifyDelay,
                TimeUnit.MINUTES);
    }
}
