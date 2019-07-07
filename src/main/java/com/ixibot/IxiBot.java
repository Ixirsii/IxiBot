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

package com.ixibot;

import com.ixibot.api.DiscordAPI;
import com.ixibot.data.BotConfiguration;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Bot logic class once startup is complete and user configuration is loaded.
 *
 * @author Ryan Porterfield
 */
@Slf4j
public class IxiBot {
    /**
     * Bot configuration.
     */
    @NonNull
    private final BotConfiguration botConfiguration;
    /**
     * Discord API interface.
     */
    @NonNull
    private final DiscordAPI discordAPI;

    /**
     * Constructor.
     *
     * @param botConfiguration Bot configuration parsed from user config file.
     */
    IxiBot(@NonNull final BotConfiguration botConfiguration) {
        this.botConfiguration = botConfiguration;
        this.discordAPI = new DiscordAPI(botConfiguration.getDiscordToken());
    }

    /**
     * Stop the bot and clean up resources.
     */
    void quit() {
        log.info("Shutting down bot");
        discordAPI.logout();
    }
}
