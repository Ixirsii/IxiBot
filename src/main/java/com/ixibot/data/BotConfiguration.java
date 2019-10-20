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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * Bot configuration and user settings POJO.
 *
 * @author Ryan Porterfield
 */
@Builder(toBuilder = true)
@JsonIgnoreProperties("defaultConfig")
@Value
public class BotConfiguration {
    /**
     * If a message starts with this prefix the bot will attempt to parse a command from it.
     */
    @NonNull
    private final String commandPrefix;
    /**
     * Is this the default bot configuration from the internal resource.
     */
    private final boolean defaultConfig;
    /**
     * Discord bot token.
     */
    @NonNull
    private final String discordToken;
    /**
     * Interval (in minutes) between Discord role verification checks.
     */
    @NonNull
    private final Long roleVerifyDelay;

    /**
     * Builder class.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @SuppressWarnings({
            "PMD.UnusedPrivateField",
            "PMD.RedundantFieldInitializer",
            "PMD.ImmutableField"
    })
    public static class BotConfigurationBuilder {
        /**
         * Default value for {@link BotConfiguration#defaultConfig}.
         */
        private boolean defaultConfig = false;
    }
}
