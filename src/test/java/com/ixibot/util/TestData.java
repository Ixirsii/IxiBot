package com.ixibot.util;

import com.ixibot.data.RoleReaction;

import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.object.util.Snowflake;

public class TestData {
    public static final String ABOUT_OPTION = "This is an option used for testing";
    public static final ReactionEmoji.Custom CUSTOM_EMOJI = ReactionEmoji.custom(
            Snowflake.of(1L), "ixiEyes", false);
    public static final String LONG_OPTION = "long-option";
    public static final char SHORT_OPTION = 's';
    public static final ReactionEmoji.Unicode UNICODE_EMOJI = ReactionEmoji.unicode("ixiNose");

    public static final RoleReaction ROLE_REACTION_1 = new RoleReaction(
            true,
            Snowflake.of(1L),
            Snowflake.of(1L),
            Snowflake.of(1L),
            CUSTOM_EMOJI,
            false,
            Snowflake.of(1L));
    public static final RoleReaction ROLE_REACTION_2 = new RoleReaction(
            false,
            Snowflake.of(2L),
            Snowflake.of(2L),
            Snowflake.of(2L),
            UNICODE_EMOJI,
            true,
            Snowflake.of(2L));
    public static final RoleReaction ROLE_REACTION_3 = new RoleReaction(
            false,
            Snowflake.of(3L),
            Snowflake.of(3L),
            Snowflake.of(3L),
            CUSTOM_EMOJI,
            false,
            Snowflake.of(3L));

    /**
     * Hide utility class constructor.
     */
    private TestData() {
    }
}
