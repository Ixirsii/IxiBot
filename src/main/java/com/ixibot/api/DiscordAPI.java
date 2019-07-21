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

import com.ixibot.data.RoleReaction;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.object.util.Snowflake;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/*
 * TODO: Refactor this to only wrap DiscordClient and move listeners into a new Listener class.
 */

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
     * Role assignment reactions.
     */
    @NonNull
    private final List<RoleReaction> roleReactions;

    /**
     * Constructor.
     *
     * @param discordClient Discord4J client.
     * @param roleReactions Role assignment reactions.
     */
    public DiscordAPI(@NonNull final DiscordClient discordClient,
                      @NonNull final List<RoleReaction> roleReactions) {
        this.discordClient = discordClient;
        this.roleReactions = roleReactions;

        registerDiscordListeners();
        discordClient.login().subscribe();
    }

    /**
     * Add a role assignment reaction to the list of reactions we listen for.
     *
     * @param roleReaction New role assignment reaction to listen for.
     */
    public void addRoleReaction(@NonNull final RoleReaction roleReaction) {
        roleReactions.add(roleReaction);
    }

    /**
     * Stop the bot and clean up resources.
     */
    public void logout() {
        log.debug("Logging out of Discord");
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
     * Remove a role assignment reaction from the list of reactions we listen for.
     *
     * @param roleReaction Role assignment reaction to no longer listen for.
     */
    public void removeRoleReaction(@NonNull final RoleReaction roleReaction) {
        roleReactions.remove(roleReaction);
    }

    /**
     * Check all role assignment reactions and update roles for all members accordingly.
     */
    public void updateAllRoles() {
        final Map<Snowflake, List<RoleReaction>> reactionsMap = roleReactions.stream()
                .filter(RoleReaction::isVerified)
                .collect(Collectors.groupingBy(RoleReaction::getGuildID));

        reactionsMap.forEach(
                (guildID, verifiedReactions) -> discordClient.getGuildById(guildID)
                        .subscribe(guild -> updateGuildRoles(guild, verifiedReactions)));
    }

    /**
     * Verify all role assignments for a guild.
     *
     * @param guild Guild to verify roles in.
     * @param verifiedReactions List of verified reactions for the guild.
     */
    private void updateGuildRoles(@NonNull final Guild guild,
                                  @NonNull final List<RoleReaction> verifiedReactions) {
        final List<Member> members = guild.getMembers().collectList().block();

        for (final RoleReaction reaction : verifiedReactions) {
            discordClient.getMessageById(reaction.getChannelID(), reaction.getMessageID())
                    .subscribe(message -> updateReactionRoles(guild, members, message, reaction));
        }
    }

    /**
     * Verify all role assignments for a message reaction.
     *
     * @param guild Guild containing message.
     * @param members Members in the guild.
     * @param message Message containing reaction.
     * @param verifiedReaction Role assignment reaction.
     */
    private void updateReactionRoles(@NonNull final Guild guild,
                                     @NonNull final List<Member> members,
                                     @NonNull final Message message,
                                     @NonNull final RoleReaction verifiedReaction) {
        final List<User> reactors = message.getReactors(verifiedReaction.getReactionEmoji())
                .collectList()
                .block();

        if (reactors == null) {
            log.error("Unable to get list of reactors for message {}", message.getId());
            return;
        }

        if (verifiedReaction.isAddVerified()) {
            verifyAddRoles(guild, reactors, message, verifiedReaction);
        }

        if (verifiedReaction.isRemoveVerified()) {
            verifyRemoveRoles(members, reactors, message, verifiedReaction);
        }
    }

    /**
     * ReactionAddEvent listener.
     *
     * @param event Event to handle.
     */
    private void reactionAddListener(@NonNull final ReactionAddEvent event) {
        final Optional<ReactionEmoji.Custom> optionalCustom = event.getEmoji().asCustomEmoji();
        final Optional<ReactionEmoji.Unicode> optionalUnicode = event.getEmoji().asUnicodeEmoji();
        final Predicate<RoleReaction> filter;

        if (optionalCustom.isPresent()) {
            final ReactionEmoji.Custom custom = optionalCustom.get();

            filter = reaction -> ((reaction.getMessageID().equals(event.getMessageId()))
                    && (reaction.getChannelID().equals(event.getChannelId()))
                    && (reaction.getReactionEmojiName().equals(custom.getName())));
        } else if (optionalUnicode.isPresent()) {
            final ReactionEmoji.Unicode unicode = optionalUnicode.get();

            filter = reaction -> ((reaction.getMessageID().equals(event.getMessageId()))
                    && (reaction.getChannelID().equals(event.getChannelId()))
                    && (reaction.getReactionEmojiName().equals(unicode.getRaw())));
        } else {
            log.error("Failed to get reaction that user added to message."
                            + "\nUser: {}\nMessage: {}\nEmoji: {}",
                    event.getUserId(),
                    event.getMessageId(),
                    event.getEmoji());
            return;
        }

        final Optional<RoleReaction> reactionOptional = roleReactions.stream()
                .filter(filter)
                .findFirst();

        if (reactionOptional.isPresent()) {
            final RoleReaction roleReaction = reactionOptional.get();

            event.getMessage().subscribe(message ->
                    message.getAuthorAsMember().subscribe(member -> {
                        final String reason = String.format(
                                "User %s reacted to message %d with %s to get role %s.",
                                member.getDisplayName(),
                                message.getId().asLong(),
                                roleReaction.getReactionEmoji(),
                                roleReaction.getRoleID());

                        log.info(reason);
                        member.addRole(roleReaction.getRoleID(), reason);
                    }));
        }
    }

    /**
     * ReactionRemoveEvent listener.
     *
     * @param event Event to handle.
     */
    private void reactionRemoveListener(@NonNull final ReactionRemoveEvent event) {
        final Optional<ReactionEmoji.Custom> optionalCustom = event.getEmoji().asCustomEmoji();
        final Optional<ReactionEmoji.Unicode> optionalUnicode = event.getEmoji().asUnicodeEmoji();
        final Predicate<RoleReaction> filter;

        if (optionalCustom.isPresent()) {
            final ReactionEmoji.Custom custom = optionalCustom.get();

            filter = reaction -> ((reaction.getMessageID().equals(event.getMessageId()))
                    && (reaction.getChannelID().equals(event.getChannelId()))
                    && (reaction.getReactionEmojiName().equals(custom.getName())));
        } else if (optionalUnicode.isPresent()) {
            final ReactionEmoji.Unicode unicode = optionalUnicode.get();

            filter = reaction -> ((reaction.getMessageID().equals(event.getMessageId()))
                    && (reaction.getChannelID().equals(event.getChannelId()))
                    && (reaction.getReactionEmojiName().equals(unicode.getRaw())));
        } else {
            log.error("Failed to get reaction that user removed from message."
                        + "\nUser: {}\nMessage: {}\nEmoji: {}",
                    event.getUserId(),
                    event.getMessageId(),
                    event.getEmoji());
            return;
        }

        final Optional<RoleReaction> reactionOptional = roleReactions.stream()
                .filter(filter)
                .findFirst();

        if (reactionOptional.isPresent()) {
            final RoleReaction roleReaction = reactionOptional.get();

            event.getMessage().subscribe(message ->
                    message.getAuthorAsMember().subscribe(member -> {
                        final String reason = String.format(
                                "User %s reacted to message %d with %s to remove role %s.",
                                member.getDisplayName(),
                                message.getId().asLong(),
                                roleReaction.getReactionEmoji(),
                                roleReaction.getRoleID());

                        log.info(reason);
                        member.removeRole(roleReaction.getRoleID(), reason);
                    }));
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
        discordClient.getEventDispatcher().on(ReactionRemoveEvent.class)
                .subscribe(this::reactionRemoveListener);
    }

    /**
     * Verify that users who have added a role reaction have the role added.
     *
     * @param guild Guild containing message.
     * @param reactors Users who have reacted with the reaction to the message.
     * @param message Message containing reaction.
     * @param verifiedReaction Role assignment reaction.
     */
    private void verifyAddRoles(@NonNull final Guild guild,
                                @NonNull final List<User> reactors,
                                @NonNull final Message message,
                                @NonNull final RoleReaction verifiedReaction) {
        for (final User reactor : reactors) {
            reactor.asMember(guild.getId()).subscribe(member -> {
                if (!member.getRoleIds().contains(verifiedReaction.getRoleID())) {
                    final String addRoleReason = String.format(
                            "User %s reacted to message %d with reaction %s but did not have role.",
                            member.getDisplayName(),
                            message.getId().asLong(),
                            verifiedReaction.getReactionEmoji());

                    log.info(addRoleReason);
                    member.addRole(verifiedReaction.getRoleID(), addRoleReason).subscribe();
                }
            });
        }
    }

    /**
     * Verify that users who have not added a role reaction don't have the role.
     *
     * @param members Members in the guild.
     * @param reactors Users who have reacted with the reaction to the message.
     * @param message Message containing reaction.
     * @param verifiedReaction Role assignment reaction.
     */
    private void verifyRemoveRoles(@NonNull final List<Member> members,
                                   @NonNull final List<User> reactors,
                                   @NonNull final Message message,
                                   @NonNull final RoleReaction verifiedReaction) {
        for (final Member member : members) {
            if (member.getRoleIds().contains(verifiedReaction.getRoleID())) {
                final Optional<User> optionalReactor = reactors.stream()
                        .filter(user -> user.getId().equals(member.getId()))
                        .findAny();

                if (!optionalReactor.isPresent()) {
                    final String removeRoleReason = String.format(
                            "User %s had not reacted to message %d with reaction %s but had role.",
                            member.getDisplayName(),
                            message.getId().asLong(),
                            verifiedReaction.getReactionEmoji());

                    log.info(removeRoleReason);
                    member.removeRole(verifiedReaction.getRoleID(), removeRoleReason).subscribe();
                }
            }
        }
    }
}
