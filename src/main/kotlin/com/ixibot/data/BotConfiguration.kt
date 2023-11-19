package com.ixibot.data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Bot configuration and user settings POJO.
 *
 * @author Ixirsii <ixirsii@ixirsii.tech>
 */
data class BotConfiguration(
        /**
         * If a message starts with this prefix the bot will attempt to parse a command from it.
         */
        val commandPrefix: String,
        /**
         * Is this the default bot configuration from the internal resource.
         */
        val isDefaultConfig: Boolean = false,
        /**
         * Should the bot exit on failure to connect to Discord.
         */
        val isDiscordRequired: Boolean,
        /**
         * Discord bot token.
         */
        val discordToken: String,
        /**
         * Interval (in minutes) between Discord role verification checks.
         */
        val roleVerifyDelay: Long) {

    @JsonCreator
    constructor(
            @JsonProperty(value = "commandPrefix", required = true)
            commandPrefix: String,
            @JsonProperty(value = "discordRequired", required = true)
            isDiscordRequired: Boolean,
            @JsonProperty(value = "discordToken", required = true)
            discordToken: String,
            @JsonProperty(value = "roleVerifyDelay", required = true)
            roleVerifyDelay: Long) : this(
            commandPrefix,
            false,
            isDiscordRequired,
            discordToken,
            roleVerifyDelay)
}
