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

package com.ixibot.module

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.google.common.eventbus.EventBus
import com.google.inject.AbstractModule
import com.google.inject.Inject
import com.google.inject.Provides
import com.google.inject.Singleton
import com.google.inject.name.Named
import com.google.inject.throwingproviders.CheckedProvides
import com.google.inject.throwingproviders.ThrowingProviderBinder
import com.ixibot.IxiBot
import com.ixibot.api.DiscordAPI
import com.ixibot.data.BotConfiguration
import com.ixibot.database.Database
import com.ixibot.provider.IxiBotProvider
import discord4j.core.DiscordClient
import discord4j.core.DiscordClientBuilder
import java.sql.SQLException
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor

/**
 * Basic Guice module.
 *
 * @author Ryan Porterfield
 */
class IxiBotModule : AbstractModule() {
    /**
     * Configure module.
     */
    override fun configure() {
        install(ThrowingProviderBinder.forModule(this))
        install(BotConfigurationModule())
        install(DatabaseModule())
    }

    /**
     * Discord4J client provider.
     *
     * @param botConfiguration Bot configuration.
     * @return Discord client.
     */
    @Inject
    @Provides
    fun discordClient(botConfiguration: BotConfiguration): DiscordClient {
        return DiscordClientBuilder(
                botConfiguration.discordToken)
                .build()
    }

    /**
     * EventBus provider.
     *
     * @return event bus.
     */
    @Provides
    @Singleton
    fun eventBus(): EventBus {
        return EventBus()
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
    @CheckedProvides(IxiBotProvider::class)
    @Inject
    @Provides
    @Throws(SQLException::class)
    fun ixiBot(
            database: Database,
            discordAPI: DiscordAPI,
            botConfiguration: BotConfiguration,
            scheduler: ScheduledExecutorService?): IxiBot {
        return IxiBot(
                database,
                discordAPI,
                database.allRoleReactions,
                botConfiguration.roleVerifyDelay,
                scheduler!!)
    }

    /**
     * Thread pool provider.
     *
     * @return scheduled thread pool executor singleton.
     */
    @Provides
    @Singleton
    fun scheduler(): ScheduledExecutorService {
        return ScheduledThreadPoolExecutor(THREAD_POOL_SIZE)
    }

    /**
     * YAML object mapper provider.
     *
     * @return YAML object mapper singleton.
     */
    @Named("yamlMapper")
    @Provides
    @Singleton
    fun yamlMapper(): ObjectMapper {
        return ObjectMapper(YAMLFactory())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
                .registerModule(Jdk8Module())
                .registerModule(KotlinModule())
    }

    companion object {
        /**
         * Core thread pool size.
         */
        private const val THREAD_POOL_SIZE = 4
    }
}