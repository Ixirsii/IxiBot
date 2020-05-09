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

import com.fasterxml.jackson.databind.ObjectMapper
import com.ixibot.CONFIG_FILE_NAME
import com.ixibot.IxiBot
import com.ixibot.USER_CONFIG_FILE
import com.ixibot.data.BotConfiguration
import com.ixibot.resourceLoader
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
        objectMapper: ObjectMapper): BotConfiguration {
    val botConfiguration: BotConfiguration
    botConfiguration = try {
        userBotConfiguration(userConfigFile, objectMapper)
    } catch (fnfe: FileNotFoundException) {
        log.debug("User configuration file not found, falling back to default configuration.")
        defaultBotConfiguration(objectMapper)
    } catch (ioe: IOException) {
        val message = "Error reading from user configuration file"
        log.error(message, ioe)
        throw IOException(message, ioe)
    }
    return botConfiguration
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
    resourceLoader.getResourceAsStream(CONFIG_FILE_NAME).use { configResource ->
        val (commandPrefix, _, isDiscordRequired, discordToken, roleVerifyDelay) = objectMapper.readValue(
                configResource,
                BotConfiguration::class.java)
        return BotConfiguration(
                commandPrefix,
                true,
                isDiscordRequired,
                discordToken,
                roleVerifyDelay)
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
@Throws(IOException::class)
private fun userBotConfiguration(
        userConfigFile: File,
        objectMapper: ObjectMapper): BotConfiguration {
    if (!userConfigFile.exists()) {
        val message = String.format(
                "User config file does not exist at %s",
                userConfigFile.absolutePath)
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
