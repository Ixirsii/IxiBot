package com.ixibot.module

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.ixibot.IxiBot
import com.ixibot.api.DiscordAPI
import com.ixibot.data.BotConfiguration
import com.ixibot.database.Database
import com.ixibot.exception.APIException
import discord4j.core.DiscordClientBuilder
import discord4j.core.GatewayDiscordClient
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor

/**
 * Core thread pool size.
 */
private const val THREAD_POOL_SIZE = 4

/**
 * Discord4J client provider.
 *
 * @param botConfiguration Bot configuration.
 * @return Discord client.
 */
fun discordClient(botConfiguration: BotConfiguration): GatewayDiscordClient {
    return DiscordClientBuilder.create(botConfiguration.discordToken)
        .build()
        .login()
        .onErrorMap { throwable: Throwable -> APIException("Failed to connect to Discord API", throwable) }
        .block()!!
}

/**
 * IxiBot provider.
 *
 * @param database Database interface.
 * @param discordAPI Discord4J wrapper.
 * @return IxiBot instance.
 */
fun ixiBot(database: Database, discordAPI: DiscordAPI): IxiBot {
    return IxiBot(database, discordAPI)
}

/**
 * YAML object mapper provider.
 *
 * @return YAML object mapper singleton.
 */
fun yamlMapper(): ObjectMapper {
    return ObjectMapper(YAMLFactory())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerModule(ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
        .registerModule(Jdk8Module())
        .registerModule(
            KotlinModule.Builder()
                .withReflectionCacheSize(512)
                .configure(KotlinFeature.NullToEmptyCollection, true)
                .configure(KotlinFeature.NullToEmptyMap, true)
                .configure(KotlinFeature.NullIsSameAsDefault, true)
                .build()
        )
}
