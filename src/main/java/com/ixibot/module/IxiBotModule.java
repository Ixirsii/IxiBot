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

import com.ixibot.data.BotConfiguration;
import com.ixibot.provider.BotConfigurationProvider;
import com.ixibot.provider.ConnectionProvider;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.throwingproviders.CheckedProvides;
import com.google.inject.throwingproviders.ThrowingProviderBinder;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import lombok.NoArgsConstructor;

/**
 * Basic Guice module.
 *
 * @author Ryan Porterfield
 */
@NoArgsConstructor
public class IxiBotModule extends AbstractModule {
    /**
     * File path to bot configuration resource.
     */
    private static final String CONFIG_RESOURCE = "/config.yaml";
    /**
     * URL/path to SQLite database file.
     */
    private static final String CONNECTION_URL = "jdbc:sqlite:sqlite.db";
    /**
     * Minimum thread pool size.
     */
    private static final int THREAD_POOL_SIZE = 1;

    /**
     * Read bot configuration from config file.
     *
     * @param objectMapper YAML object mapper.
     * @return bot configuration.
     * @throws IOException on error reading from config file.
     */
    @CheckedProvides(BotConfigurationProvider.class)
    @Inject
    @Provides
    @Singleton
    public BotConfiguration botConfiguration(
            @Named("yamlMapper") final ObjectMapper objectMapper) throws IOException {
        try (InputStream configResource = getClass().getResourceAsStream(CONFIG_RESOURCE)) {
            return objectMapper.readValue(configResource, BotConfiguration.class);
        }
    }

    /**
     * Configure module.
     */
    @Override
    protected void configure() {
        install(ThrowingProviderBinder.forModule(this));
    }

    /**
     * JDBC connection provider.
     *
     * @return JDBC connection.
     * @throws ClassNotFoundException on failure to load JDBC driver.
     * @throws SQLException           if a database access error occurs.
     */
    @CheckedProvides(ConnectionProvider.class)
    @Provides
    public Connection connection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");

        return DriverManager.getConnection(CONNECTION_URL);
    }

    /**
     * Discord4J client provider.
     *
     * @param botConfiguration Bot configuration.
     * @return Discord client.
     */
    @Inject
    @Provides
    public DiscordClient discordClient(final BotConfiguration botConfiguration) {
        return new DiscordClientBuilder(
                botConfiguration.getDiscordToken())
                .build();
    }

    /**
     * Thread pool provider.
     *
     * @return scheduled thread pool executor singleton.
     */
    @Provides
    @Singleton
    public ScheduledThreadPoolExecutor scheduler() {
        return new ScheduledThreadPoolExecutor(
                THREAD_POOL_SIZE,
                Executors.defaultThreadFactory());
    }

    /**
     * YAML object mapper provider.
     *
     * @return YAML object mapper singleton.
     */
    @Named("yamlMapper")
    @Provides
    @Singleton
    public ObjectMapper yamlMapper() {
        return new ObjectMapper(new YAMLFactory())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
                .registerModule(new Jdk8Module());
    }
}
