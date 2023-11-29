package com.ixibot.module

import arrow.core.Option
import arrow.core.none
import arrow.core.some
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.google.common.eventbus.EventBus
import com.ixibot.CONFIG_DIRECTORY
import com.ixibot.CONFIG_FILE_NAME
import com.ixibot.data.BotConfiguration
import com.ixibot.exception.APIException
import com.ixibot.listener.ConsoleListener
import com.ixibot.listener.DiscordListener
import discord4j.core.DiscordClientBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Empty class used for module logging.
 */
class BotModule

/**
 * Module logger.
 */
private val log: Logger = LoggerFactory.getLogger(BotModule::class.java)

/**
 * IxiBot module.
 */
val botModule: Module = module {
    /**
     * Bot configuration provider.
     */
    single { (userConfigFile: File, objectMapper: ObjectMapper) ->
        if (userConfigFile.exists()) {
            objectMapper.readValue(userConfigFile, BotConfiguration::class.java).some()
        } else {
            log.warn("User config file does not exist at {}", userConfigFile.absolutePath)

            none()
        }
    }

    /**
     * Console listener provider.
     */
    single { ConsoleListener(get(), get()) }

    /**
     * Console listener coroutine context
     */
    single(named("consoleListenerContext")) { CoroutineScope(Dispatchers.IO).coroutineContext }

    /**
     * Discord listener provider.
     */
    single { DiscordListener(get())  }

    /**
     * Discord4J client provider.
     */
    single {
        DiscordClientBuilder.create(get())
            .build()
            .login()
            .onErrorMap { throwable: Throwable -> APIException("Failed to connect to Discord API", throwable) }
            .block()!!
    }

    /**
     * Event bus provider.
     */
    single { EventBus() }

    /**
     * YAML object mapper provider.
     */
    single {
        ObjectMapper(YAMLFactory())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
            .registerModule(Jdk8Module())
            .registerModule(
                KotlinModule.Builder()
                    .configure(KotlinFeature.NullToEmptyCollection, true)
                    .configure(KotlinFeature.NullToEmptyMap, true)
                    .configure(KotlinFeature.NullIsSameAsDefault, true)
                    .build()
            )
    }

    /**
     * Default config resource file path
     */
    single(named("resourceFilePath")) { CONFIG_FILE_NAME }

    /**
     * User config file path.
     */
    single(named("userConfigFilePath")) { CONFIG_DIRECTORY + CONFIG_FILE_NAME }

    /**
     * User config file provider.
     */
    single { File(get<String>(named("userConfigFilePath"))) }
}
