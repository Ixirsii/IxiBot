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

package com.ixibot.data;

import java.util.Optional;

import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.object.util.Snowflake;
import lombok.Data;
import lombok.NonNull;

/**
 * Role assignment reaction POJO.
 *
 * @author Ryan Porterfield
 */
@Data
public class RoleReaction {
    /**
     * Is the role add verified.
     *
     * <p>
     *     If this is true, the bot will periodically check all users who have reacted with this
     *     reaction and assign them the role if they don't have it.
     * </p>
     */
    private final boolean addVerified;
    /**
     * Channel ID containing the message.
     */
    @NonNull
    private final Snowflake channelID;
    /**
     * Guild ID containing the channel.
     */
    @NonNull
    private final Snowflake guildID;
    /**
     * Message ID containing the reaction.
     */
    @NonNull
    private final Snowflake messageID;
    /**
     * Reaction emoji name/raw.
     */
    @NonNull
    private final ReactionEmoji reactionEmoji;
    /**
     * Is the role remove verified.
     *
     * <p>
     *     If this is true, the bot will periodically check all users and remove the role from
     *     all users in the guild who haven't reacted with this reaction.
     * </p>
     */
    private final boolean removeVerified;
    /**
     * Role ID to (un)assign.
     */
    @NonNull
    private final Snowflake roleID;

    /**
     * Get boxed reaction emoji ID.
     *
     * @return reaction emoji ID if reaction emoji is a custom emoji, otherwise null.
     */
    public Long getBoxedReactionEmojiID() {
        final Optional<ReactionEmoji.Custom> optionalCustom = reactionEmoji.asCustomEmoji();

        if (optionalCustom.isPresent()) {
            return optionalCustom.get().getId().asLong();
        }

        return null;
    }

    /**
     * Get the name (unicode raw or custom name) of the reaction emoji.
     *
     * @return name of the reaction emoji.
     */
    public String getReactionEmojiName() {
        final Optional<ReactionEmoji.Custom> optionalCustom = reactionEmoji.asCustomEmoji();
        final Optional<ReactionEmoji.Unicode> optionalUnicode = reactionEmoji.asUnicodeEmoji();

        if (optionalCustom.isPresent()) {
            return optionalCustom.get().getName();
        } else if (optionalUnicode.isPresent()) {
            return optionalUnicode.get().getRaw();
        } else {
            return "";
        }
    }

    /**
     * Is the role add or remove verified.
     *
     * @return {@code true} if either addVerified or removeVerified is {@code true},
     *                      otherwise {@code false}.
     * @see RoleReaction#addVerified
     * @see RoleReaction#removeVerified
     */
    public boolean isVerified() {
        return addVerified || removeVerified;
    }
}
