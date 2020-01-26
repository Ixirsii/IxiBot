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

package com.ixibot.module;

import com.ixibot.IxiBot;
import com.ixibot.data.BotConfiguration;
import com.ixibot.provider.BotConfigurationProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.throwingproviders.CheckedProvides;
import com.google.inject.throwingproviders.ThrowingProviderBinder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Bot configuration Guice module.
 *
 * @author Ryan Porterfield.
 */
@NoArgsConstructor
@Slf4j
/* default */ class BotConfigurationModule extends AbstractModule {

    /**
     * Configure module.
     */
    @Override
    protected void configure() {
        install(ThrowingProviderBinder.forModule(this));
    }

    /**
     * Bot configuration provider.
     *
     * @param userConfigFile User's configuration file.
     * @param objectMapper YAML object mapper.
     * @return bot configuration.
     * @throws IOException on error reading from config file.
     */
    @CheckedProvides(BotConfigurationProvider.class)
    @Inject
    @Provides
    @Singleton
    public BotConfiguration botConfiguration(
            @Named("userConfigFile") final File userConfigFile,
            @Named("yamlMapper") final ObjectMapper objectMapper) throws IOException {
        BotConfiguration botConfiguration;

        try {
            botConfiguration = userBotConfiguration(userConfigFile, objectMapper);
        } catch (final FileNotFoundException fnfe) {
            log.debug("User configuration file not found, falling back to default configuration.");

            botConfiguration = defaultBotConfiguration(objectMapper);
        } catch (final IOException ioe) {
            final String message = "Error reading from user configuration file";
            log.error(message, ioe);

            throw new IOException(message, ioe);
        }

        return botConfiguration;
    }

    /**
     * Read bot configuration from default config file.
     *
     * @param objectMapper YAML object mapper.
     * @return default bot configuration.
     * @throws IOException on error reading from config file.
     */
    private BotConfiguration defaultBotConfiguration(@NonNull final ObjectMapper objectMapper)
            throws IOException {
        try (InputStream configResource = getClass().getResourceAsStream(IxiBot.CONFIG_RESOURCE)) {
            final BotConfiguration defaultConfig = objectMapper.readValue(
                    configResource,
                    BotConfiguration.class);

            return new BotConfiguration(
                    defaultConfig.getCommandPrefix(),
                    true,
                    defaultConfig.isDiscordRequired(),
                    defaultConfig.getDiscordToken(),
                    defaultConfig.getRoleVerifyDelay());
        }
    }

    /**
     * Get bot configuration from user's config file.
     *
     * @param userConfigFile User's configuration file.
     * @param objectMapper YAML object mapper.
     * @return User's bot configuration.
     * @throws IOException on error reading from config file.
     */
    private BotConfiguration userBotConfiguration(
            @NonNull final File userConfigFile,
            @NonNull final ObjectMapper objectMapper) throws IOException {

        if (!userConfigFile.exists()) {
            final String message = String.format(
                    "User config file does not exist at %s",
                    userConfigFile.getAbsolutePath());

            log.warn(message);
            throw new FileNotFoundException(message);
        }

        return objectMapper.readValue(userConfigFile, BotConfiguration.class);
    }

    /**
     * User config file provider.
     *
     * <p>
     *     This is currently set up to allow a custom file path to be passed to
     *     {@link BotConfigurationModule#botConfiguration(File, ObjectMapper)} which allows the
     *     method to be tested easier, but also allows us flexibility to take a custom config
     *     file path as a command line argument in the future.
     * </p>
     *
     * @return user config file.
     */
    @Named("userConfigFile")
    @Provides
    @Singleton
    public File userConfigFile() {
        return new File(IxiBot.USER_CONFIG_FILE);
    }
}
