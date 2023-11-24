package com.ixibot.module

import arrow.core.Option
import arrow.core.none
import arrow.core.some
import com.fasterxml.jackson.databind.ObjectMapper
import com.ixibot.IxiBot
import com.ixibot.USER_CONFIG_FILE
import com.ixibot.data.BotConfiguration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

/**
 * Logger.
 */
private val log: Logger = LoggerFactory.getLogger(IxiBot::class.java)

/**
 * Get bot configuration from user's config file.
 *
 * @param userConfigFile User's configuration file.
 * @param objectMapper YAML object mapper.
 * @return User's bot configuration.
 * @throws IOException on error reading from config file.
 */
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
