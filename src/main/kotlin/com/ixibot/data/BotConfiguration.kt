package com.ixibot.data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

// TODO: Figure out a better way to set isDefaultConfig
/**
 * Bot configuration and user settings POJO.
 *
 * @author Ixirsii <ixirsii@ixirsii.tech>
 */
data class BotConfiguration @JsonCreator constructor(
    /**
     * If a message starts with this prefix the bot will attempt to parse a command from it.
     */
    @JsonProperty(value = "commandPrefix", required = true)
    val commandPrefix: String,
    /**
     * Discord bot token.
     */
    @JsonProperty(value = "discordToken", required = true)
    val discordToken: String,
)
