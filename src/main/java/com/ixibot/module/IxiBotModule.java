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
import com.ixibot.api.DiscordAPI;
import com.ixibot.data.BotConfiguration;
import com.ixibot.database.Database;
import com.ixibot.provider.IxiBotProvider;

import java.sql.SQLException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.common.eventbus.EventBus;
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
     * Core thread pool size.
     */
    private static final int THREAD_POOL_SIZE = 4;

    /**
     * Configure module.
     */
    @Override
    protected void configure() {
        install(ThrowingProviderBinder.forModule(this));
        install(new BotConfigurationModule());
        install(new DatabaseModule());
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
     * EventBus provider.
     *
     * @return event bus.
     */
    @Provides
    @Singleton
    public EventBus eventBus() {
        return new EventBus();
    }

    /**
     * IxiBot provider.
     *
     * @param database Database interface.
     * @param discordAPI Discord4J wrapper.
     * @param botConfiguration Bot configuration.
     * @param scheduler Thread pool scheduler.
     * @return IxiBot instance.
     * @throws SQLException on error reading from database.
     */
    @CheckedProvides(IxiBotProvider.class)
    @Inject
    @Provides
    public IxiBot ixiBot(
            final Database database,
            final DiscordAPI discordAPI,
            final BotConfiguration botConfiguration,
            final ScheduledExecutorService scheduler) throws SQLException {
        return new IxiBot(
                database,
                discordAPI,
                database.getAllRoleReactions(),
                botConfiguration.getRoleVerifyDelay(),
                scheduler);
    }

    /**
     * Thread pool provider.
     *
     * @return scheduled thread pool executor singleton.
     */
    @Provides
    @Singleton
    public ScheduledExecutorService scheduler() {
        return new ScheduledThreadPoolExecutor(THREAD_POOL_SIZE);
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
                .registerModule(new Jdk8Module())
                .registerModule(new KotlinModule());
    }
}
