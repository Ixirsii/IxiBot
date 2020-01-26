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

package com.ixibot.util;

import com.fasterxml.jackson.module.kotlin.KotlinModule;
import com.ixibot.data.BotConfiguration;
import com.ixibot.data.RoleReaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.object.util.Snowflake;

public class TestData {
    public static final String ABOUT_OPTION = "This is an option used for testing";
    public static final String CONFIG_RESOURCE = "/config.yaml";
    public static final String COMMAND_PREFIX = "./";
    public static final String DISCORD_TOKEN = "discordToken";
    public static final String LONG_OPTION = "long-option";
    public static final long ROLE_VERIFY_DELAY = 10L;
    public static final char SHORT_OPTION = 's';

    public static final String INVALID_CONFIGURATION = String.format(
            "commandPrefix: %s%ndiscordToken: %s",
            COMMAND_PREFIX,
            DISCORD_TOKEN);
    public static final String VALID_CONFIGURATION = String.format(
            "commandPrefix: %s%ndiscordRequired: %b%ndiscordToken: %s%nroleVerifyDelay: %d",
            COMMAND_PREFIX,
            true,
            DISCORD_TOKEN,
            ROLE_VERIFY_DELAY);

    public static final BotConfiguration DEFAULT_CONFIG = new BotConfiguration(
            COMMAND_PREFIX,
            true,
            true,
            DISCORD_TOKEN,
            ROLE_VERIFY_DELAY);
    public static final BotConfiguration USER_CONFIG = new BotConfiguration(
            COMMAND_PREFIX,
            false,
            true,
            DISCORD_TOKEN,
            ROLE_VERIFY_DELAY);

    public static final ReactionEmoji.Custom CUSTOM_EMOJI = ReactionEmoji.custom(
            Snowflake.of(1L), "ixiEyes", false);
    public static final ReactionEmoji.Unicode UNICODE_EMOJI = ReactionEmoji.unicode("ixiNose");

    public static final RoleReaction ROLE_REACTION_1 = new RoleReaction(
            Snowflake.of(1L),
            Snowflake.of(1L),
            true,
            false,
            Snowflake.of(1L),
            CUSTOM_EMOJI,
            Snowflake.of(1L));
    public static final RoleReaction ROLE_REACTION_2 = new RoleReaction(
            Snowflake.of(2L),
            Snowflake.of(2L),
            false,
            true,
            Snowflake.of(2L),
            UNICODE_EMOJI,
            Snowflake.of(2L));
    public static final RoleReaction ROLE_REACTION_3 = new RoleReaction(
            Snowflake.of(3L),
            Snowflake.of(3L),
            false,
            false,
            Snowflake.of(3L),
            CUSTOM_EMOJI,
            Snowflake.of(3L));

    public static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
            .registerModule(new Jdk8Module())
            .registerModule(new KotlinModule());

    /**
     * Hide utility class constructor.
     */
    private TestData() {
    }
}
