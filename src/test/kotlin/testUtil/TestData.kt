/*
 * Copyright (c) 2020, Ryan Porterfield
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *
 *     3. Neither the name of the copyright holder nor the names of its
 *        contributors may be used to endorse or promote products derived from
 *        this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 *  IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *  PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 *  HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package testUtil

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.ixibot.data.RoleReaction
import discord4j.common.util.Snowflake
import discord4j.core.`object`.reaction.ReactionEmoji

const val ABOUT_OPTION = "This is an option used for testing"
const val LONG_OPTION = "long-option"
const val SHORT_OPTION = 's'

val CUSTOM_EMOJI: ReactionEmoji.Custom = ReactionEmoji.custom(
        Snowflake.of(1L), "ixiEyes", false)
val UNICODE_EMOJI: ReactionEmoji.Unicode = ReactionEmoji.unicode("ixiNose")

val ROLE_REACTION_1 = RoleReaction(
        channelID = Snowflake.of(1L),
        guildID = Snowflake.of(1L),
        messageID = Snowflake.of(1L),
        reactionEmoji = CUSTOM_EMOJI,
        roleID = Snowflake.of(1L))
val ROLE_REACTION_2 = RoleReaction(
        channelID = Snowflake.of(2L),
        guildID = Snowflake.of(2L),
        messageID = Snowflake.of(2L),
        reactionEmoji = UNICODE_EMOJI,
        roleID = Snowflake.of(2L))
val ROLE_REACTION_3 = RoleReaction(
        channelID = Snowflake.of(3L),
        guildID = Snowflake.of(3L),
        messageID = Snowflake.of(3L),
        reactionEmoji = CUSTOM_EMOJI,
        roleID = Snowflake.of(3L))
