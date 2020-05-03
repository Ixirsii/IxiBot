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

package com.ixibot.data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Bot configuration and user settings POJO.
 *
 * @author Ryan Porterfield
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
            roleVerifyDelay: Long
    ) : this(
            commandPrefix,
            false,
            isDiscordRequired,
            discordToken,
            roleVerifyDelay)
}
