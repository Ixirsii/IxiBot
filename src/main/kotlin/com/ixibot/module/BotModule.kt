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
import com.ixibot.USER_CONFIG_FILE
import com.ixibot.data.BotConfiguration
import com.ixibot.exception.APIException
import com.ixibot.logging.Logging
import com.ixibot.logging.LoggingImpl
import discord4j.core.DiscordClientBuilder
import discord4j.core.GatewayDiscordClient
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import java.io.File
import java.io.IOException

@ComponentScan("com.ixibot")
@Module
class BotModule : Logging by LoggingImpl<BotModule>() {
    /**
     * Get bot configuration from user's config file.
     *
     * @param userConfigFile User's configuration file.
     * @param objectMapper YAML object mapper.
     * @return User's bot configuration.
     * @throws IOException on error reading from config file.
     */
    @Single
    @Throws(IOException::class)
    fun botConfiguration(
        userConfigFile: File,
        objectMapper: ObjectMapper,
    ): Option<BotConfiguration> {
        return if (userConfigFile.exists()) {
            objectMapper.readValue(userConfigFile, BotConfiguration::class.java).some()
        } else {
            log.warn("User config file does not exist at {}", userConfigFile.absolutePath)

            none()
        }
    }

    /**
     * Discord4J client provider.
     *
     * @param botConfiguration Bot configuration.
     * @return Discord client.
     */
    @Single
    fun discordClient(botConfiguration: BotConfiguration): GatewayDiscordClient {
        return DiscordClientBuilder.create(botConfiguration.discordToken)
            .build()
            .login()
            .onErrorMap { throwable: Throwable -> APIException("Failed to connect to Discord API", throwable) }
            .block()!!
    }

    /**
     * Event bus.
     */
    @Single
    fun eventBus(): EventBus = EventBus()

    /**
     * User config file provider.
     *
     * This is currently set up to allow a custom file path to be passed to [botConfiguration] which allows the method to
     * be tested easier, but also allows us flexibility to take a custom config file path as a command line argument in the
     * future.
     *
     *
     * @return user config file.
     */
    @Single
    fun userConfigFile(): File {
        return File(USER_CONFIG_FILE)
    }

    /**
     * YAML object mapper provider.
     *
     * @return YAML object mapper singleton.
     */
    @Single
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
}