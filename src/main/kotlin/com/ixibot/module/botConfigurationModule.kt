package com.ixibot.module

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.io.Resources
import com.ixibot.CONFIG_FILE_NAME
import com.ixibot.IxiBot
import com.ixibot.USER_CONFIG_FILE
import com.ixibot.data.BotConfiguration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

/**
 * Logger.
 */
private val log: Logger = LoggerFactory.getLogger(IxiBot::class.java)

/**
 * Bot configuration provider.
 *
 * @param userConfigFile User's configuration file.
 * @param objectMapper YAML object mapper.
 * @return bot configuration.
 * @throws IOException on error reading from config file.
 */
@Throws(IOException::class)
fun botConfiguration(
    userConfigFile: File,
    objectMapper: ObjectMapper,
): BotConfiguration {
    return try {
        userBotConfiguration(userConfigFile, objectMapper)
    } catch (ex: FileNotFoundException) {
        log.debug("User configuration file not found, falling back to default configuration.")
        defaultBotConfiguration(objectMapper)
    } catch (ex: IOException) {
        val message = "Error reading from user configuration file"
        log.error(message, ex)
        throw IOException(message, ex)
    }
}

/**
 * Read bot configuration from default config file.
 *
 * @param objectMapper YAML object mapper.
 * @return default bot configuration.
 * @throws IOException on error reading from config file.
 */
@Throws(IOException::class)
private fun defaultBotConfiguration(objectMapper: ObjectMapper): BotConfiguration {
    val (commandPrefix, _, isDiscordRequired, discordToken, roleVerifyDelay) = objectMapper.readValue(
        Resources.toByteArray(
            Resources.getResource(CONFIG_FILE_NAME)
        ), BotConfiguration::class.java
    )

    return BotConfiguration(
        commandPrefix,
        true,
        isDiscordRequired,
        discordToken,
        roleVerifyDelay
    )
}

/**
 * Get bot configuration from user's config file.
 *
 * @param userConfigFile User's configuration file.
 * @param objectMapper YAML object mapper.
 * @return User's bot configuration.
 * @throws IOException on error reading from config file.
 */
@Throws(IOException::class)
private fun userBotConfiguration(
    userConfigFile: File,
    objectMapper: ObjectMapper,
): BotConfiguration {
    if (!userConfigFile.exists()) {
        val message = String.format(
            "User config file does not exist at %s",
            userConfigFile.absolutePath
        )
        log.warn(message)
        throw FileNotFoundException(message)
    }
    return objectMapper.readValue(userConfigFile, BotConfiguration::class.java)
}

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
fun userConfigFile(): File {
    return File(USER_CONFIG_FILE)
}
