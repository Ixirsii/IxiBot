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

package com.ixibot.api;

import java.util.Optional;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveAllEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Discord4J wrapper.
 *
 * @author Ryan Porterfield
 */
@Slf4j
public class DiscordAPI {
    /**
     * Discord client.
     */
    @NonNull
    private final DiscordClient discordClient;

    /**
     * Constructor.
     *
     * @param discordToken Discord bot token.
     */
    public DiscordAPI(@NonNull final String discordToken) {
        this.discordClient = new DiscordClientBuilder(discordToken)
                .build();

        registerDiscordListeners();
        discordClient.login().subscribe();
    }

    /**
     * Stop the bot and clean up resources.
     */
    public void logout() {
        log.info("Logging out of Discord");
        discordClient.logout().block();
    }

    /**
     * MessageCreateEvent listener.
     *
     * @param event Event to handle.
     */
    private void messageCreateListener(@NonNull final MessageCreateEvent event) {
        final Optional<Member> optionalMember = event.getMember();
        final Message message = event.getMessage();

        log.info("#{} [{}]: {}",
                message.getChannelId().asLong(),
                optionalMember.map(Member::getDisplayName).orElse(""),
                message.getContent().orElse(""));
    }

    /**
     * ReactionAddEvent listener.
     *
     * @param event Event to handle.
     */
    private void reactionAddListener(@NonNull final ReactionAddEvent event) {
        final long channelId = event.getChannelId().asLong();
        final long messageId = event.getMessageId().asLong();
        final long userId = event.getUserId().asLong();
        final ReactionEmoji reactionEmoji = event.getEmoji();
        final Optional<ReactionEmoji.Custom> optionalCustom =
                reactionEmoji.asCustomEmoji();
        final Optional<ReactionEmoji.Unicode> optionalUnicode =
                reactionEmoji.asUnicodeEmoji();

        if (optionalCustom.isPresent()) {
            final ReactionEmoji.Custom custom = optionalCustom.get();
            log.info("User {} is adding {} ({}) reaction to #{}: {}",
                    userId,
                    custom.getName(),
                    custom.getId(),
                    channelId,
                    messageId);
        } else if (optionalUnicode.isPresent()) {
            final ReactionEmoji.Unicode unicode = optionalUnicode.get();
            log.info("User {} is adding {} reacting to #{}: {}",
                    userId,
                    unicode.getRaw(),
                    channelId,
                    messageId);
        }
    }

    /**
     * ReactionRemoveEvent listener.
     *
     * @param event Event to handle.
     */
    private void reactionRemoveAllListener(@NonNull final ReactionRemoveAllEvent event) {
        log.info("Removing all reactions on post #{}: {}",
                event.getChannel(),
                event.getMessageId());
    }

    /**
     * ReactionRemoveEvent listener.
     *
     * @param event Event to handle.
     */
    private void reactionRemoveListener(@NonNull final ReactionRemoveEvent event) {
        final long channelId = event.getChannelId().asLong();
        final long messageId = event.getMessageId().asLong();
        final long userId = event.getUserId().asLong();
        final ReactionEmoji reactionEmoji = event.getEmoji();
        final Optional<ReactionEmoji.Custom> optionalCustom =
                reactionEmoji.asCustomEmoji();
        final Optional<ReactionEmoji.Unicode> optionalUnicode =
                reactionEmoji.asUnicodeEmoji();

        if (optionalCustom.isPresent()) {
            final ReactionEmoji.Custom custom = optionalCustom.get();
            log.info("User {} is removing {} ({}) reaction to #{}: {}",
                    userId,
                    custom.getName(),
                    custom.getId(),
                    channelId,
                    messageId);
        } else if (optionalUnicode.isPresent()) {
            final ReactionEmoji.Unicode unicode = optionalUnicode.get();
            log.info("User {} is removing {} reacting to #{}: {}",
                    userId,
                    unicode.getRaw(),
                    channelId,
                    messageId);
        }
    }

    /**
     * Register Discord event listeners.
     */
    private void registerDiscordListeners() {
        discordClient.getEventDispatcher().on(MessageCreateEvent.class)
                .subscribe(this::messageCreateListener);
        discordClient.getEventDispatcher().on(ReactionAddEvent.class)
                .subscribe(this::reactionAddListener);
        discordClient.getEventDispatcher().on(ReactionRemoveAllEvent.class)
                .subscribe(this::reactionRemoveAllListener);
        discordClient.getEventDispatcher().on(ReactionRemoveEvent.class)
                .subscribe(this::reactionRemoveListener);
    }
}
