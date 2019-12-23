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
import com.ixibot.event.DiscordReactionEvent;
import com.ixibot.event.RoleReactionEvent;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.eventbus.Subscribe;
import discord4j.core.object.reaction.ReactionEmoji;
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
     * Bot configuration file.
     */
    private static final String CONFIG_FILE_NAME = "config.yaml";

    /**
     * Bot configuration directory.
     */
    public static final String CONFIG_DIRECTORY = "config/";
    /**
     * File path to bot configuration resource.
     */
    public static final String CONFIG_RESOURCE = "/" + CONFIG_FILE_NAME;
    /**
     * Config file configured by the user.
     */
    public static final String USER_CONFIG_FILE = CONFIG_DIRECTORY + CONFIG_FILE_NAME;

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
     * Role assignment reactions.
     */
    @NonNull
    private final List<RoleReaction> roleReactions;
    /**
     * Interval (in minutes) between Discord role verification checks.
     */
    private final long roleVerifyDelay;
    /**
     * Thread pool executor for scheduled async actions.
     */
    @NonNull
    private final ScheduledExecutorService scheduler;

    /**
     * {@code true} while bot is running, {@code folse} when bot is terminating.
     */
    private boolean running = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        log.trace("Shutting down bot");

        discordAPI.logout();
        shutdownScheduler();

        try {
            database.close();
        } catch (final SQLException sqle) {
            log.error("Caught SQLException while attempting to close database", sqle);
        }
    }

    /**
     * Initialize bot instance.
     *
     * @throws ConnectException on failure to connect to API.
     */
    public void init() throws ConnectException {
        discordAPI.init();
        running = true;
    }

    // TODO: Fix this
    /**
     * DiscordReactionEvent subscriber.
     *
     * @param event Event published to the event bus.
     */
    @Subscribe
    public void onDiscordReactionEvent(@NonNull final DiscordReactionEvent event) {
        final Predicate<RoleReaction> filter;
        final Optional<ReactionEmoji.Custom> optionalCustom = event.getReactionEmoji()
                .asCustomEmoji();
        final Optional<ReactionEmoji.Unicode> optionalUnicode = event.getReactionEmoji()
                .asUnicodeEmoji();

        if (optionalCustom.isPresent()) {
            final ReactionEmoji.Custom custom = optionalCustom.get();

            filter = reaction -> ((reaction.getMessageID().equals(event.getMessageID()))
                    && (reaction.getChannelID().equals(event.getChannelID()))
                    && (reaction.getReactionEmojiName().equals(custom.getName()))
                    && (reaction.getBoxedReactionEmojiID().equals(custom.getId().asLong())));
        } else if (optionalUnicode.isPresent()) {
            final ReactionEmoji.Unicode unicode = optionalUnicode.get();

            filter = reaction -> ((reaction.getMessageID().equals(event.getMessageID()))
                    && (reaction.getChannelID().equals(event.getChannelID()))
                    && (reaction.getReactionEmojiName().equals(unicode.getRaw())));
        } else {
            log.error("Failed to get reaction that user added to message."
                            + "\nUser: {}\nMessage: {}\nEmoji: {}",
                    event.getUserID(),
                    event.getMessageID(),
                    event.getReactionEmoji());
            return;
        }

        final Optional<RoleReaction> reactionOptional = roleReactions.stream()
                .filter(filter)
                .findFirst();

        if (reactionOptional.isPresent()) {
            final String reasonFormat = (event.isAdd())
                    ? "User %s reacted to message %d with %s to get role %s."
                    : "User %s reacted to message %d with %s to remove role %s.";
            final RoleReaction roleReaction = reactionOptional.get();

            event.getMessageMono().subscribe(message ->
                    message.getAuthorAsMember().subscribe(member -> {
                        final String reason = String.format(
                                reasonFormat,
                                member.getDisplayName(),
                                message.getId().asLong(),
                                roleReaction.getReactionEmoji(),
                                roleReaction.getRoleID());

                        log.info(reason);

                        if (event.isAdd()) {
                            member.addRole(roleReaction.getRoleID(), reason);
                        } else {
                            member.removeRole(roleReaction.getRoleID(), reason);
                        }
                    }));
        }
    }

    /**
     * RoleReactionEvent subscriber.
     *
     * @param event Event published to event bus.
     */
    @Subscribe
    public void onRoleReactionEvent(@NonNull final RoleReactionEvent event) {
        final RoleReaction roleReaction = event.getRoleReaction();

        if (event.isCreate()) {
            roleReactions.add(roleReaction);
        } else {
            roleReactions.remove(roleReaction);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        scheduler.scheduleAtFixedRate(
                () -> discordAPI.updateAllRoles(roleReactions.stream()
                        .filter(RoleReaction::isVerified)
                        .collect(Collectors.groupingBy(RoleReaction::getGuildID))),
                0,
                roleVerifyDelay,
                TimeUnit.MINUTES);

        try {
            while (running) {
                // Sleep for 1 second (1s * 1000ms/s = 1000ms)
                Thread.sleep(1000);
            }
        } catch (final InterruptedException ie) {
            log.error(
                    "Thread \"{}\" interrupted while sleeping.",
                    Thread.currentThread().getName(),
                    ie);
        }
    }

    /**
     * Shutdown thread pool scheduler.
     */
    private void shutdownScheduler() {
        scheduler.shutdown();

        // Wait for the thread pool to shut down
        try {
            // Wait for tasks to terminate
            if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                // (Attempt to) force stop thread pool
                scheduler.shutdownNow();

                // Wait for tasks to respond to being cancelled
                if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                    log.error("Failed to shut down thread pool");
                }
            }
        } catch (final InterruptedException ie) {
            log.error("Thread interrupted while waiting for thread pool to shutdown", ie);

            // Verify that thread pool is shutdown
            scheduler.shutdownNow();

            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
