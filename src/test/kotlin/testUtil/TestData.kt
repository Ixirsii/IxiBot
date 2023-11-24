package testUtil

import com.ixibot.data.RoleReaction
import discord4j.common.util.Snowflake

val ROLE_REACTION_1 = RoleReaction(
        channelID = Snowflake.of(1L),
        guildID = Snowflake.of(1L),
        messageID = Snowflake.of(1L),
        name = "ixiEyes",
        roleID = Snowflake.of(1L))
val ROLE_REACTION_2 = RoleReaction(
        channelID = Snowflake.of(2L),
        guildID = Snowflake.of(2L),
        messageID = Snowflake.of(2L),
        name = "ixiNose",
        roleID = Snowflake.of(2L))
val ROLE_REACTION_3 = RoleReaction(
        channelID = Snowflake.of(3L),
        guildID = Snowflake.of(3L),
        messageID = Snowflake.of(3L),
        name = "ixiEyes",
        roleID = Snowflake.of(3L))
