/*
 * Copyright (c) 2019, Ryan Porterfield
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *
 *     3. Neither the name of the copyright holder nor the names of its
 *        contributors may be used to endorse or promote products derived from
 *        this software without specific prior written permission.
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

package com.ixibot

import com.google.common.eventbus.EventBus
import com.ixibot.api.DiscordAPI
import com.ixibot.data.BotConfiguration
import com.ixibot.database.Database
import com.ixibot.listener.ConsoleListener
import com.ixibot.listener.DiscordListener
import com.ixibot.module.botConfiguration
import com.ixibot.module.connection
import com.ixibot.module.database
import com.ixibot.module.discordClient
import com.ixibot.module.ixiBot
import com.ixibot.module.scheduler
import com.ixibot.module.userConfigFile
import com.ixibot.module.yamlMapper
import com.ixibot.subscriber.DatabaseSubscriber
import com.ixibot.subscriber.DiscordSubscriber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.net.ConnectException

/**
 * Logger.
 */
private val log: Logger = LoggerFactory.getLogger(IxiBot::class.java)

/**
 * Main method.
 */
fun main() {
    val botConfiguration: BotConfiguration = botConfiguration(userConfigFile(), yamlMapper())

    if (botConfiguration.isDefaultConfig) {
        val configFile = File(USER_CONFIG_FILE)

        generateUserConfig(configFile)
    } else {
        val database: Database = database(connection())
        val eventBus = EventBus()
        val ixiBot: IxiBot = ixiBot(
                database,
                DiscordAPI(
                        discordClient(botConfiguration),
                        DiscordListener(eventBus),
                        botConfiguration.isDiscordRequired),
                botConfiguration,
                scheduler())
        val consoleListener = ConsoleListener(eventBus, CoroutineScope(Dispatchers.Default).coroutineContext)

        listOf<Any>(DatabaseSubscriber(database), DiscordSubscriber(database))
                .forEach(eventBus::register)

        run(consoleListener, ixiBot)
    }
}

/**
 * Write default configuration file and exit.
 *
 * @param configFile File path to user config file.
 */
fun generateUserConfig(configFile: File) {
    try {
        resourceLoader.getResourceAsStream(CONFIG_FILE_NAME).use { configResource ->
            log.debug("Writing new user config file to \"{}\"", configFile.absolutePath)
            FileUtils.copyToFile(configResource, configFile)
        }

        log.info(
                "Generated new user config file at \"{}\". Please customize your configuration then restart the bot",
                configFile.absolutePath)
    } catch (ioe: IOException) {
        log.error(
                "Encountered exception while trying to write new user config file to \"{}\"",
                configFile.absolutePath,
                ioe)
    }
}

/**
 * Run bot.
 *
 * This function handles initialization and cleanup of parameters passed to it.
 *
 * @param consoleListener Coroutine which listens for console input.
 * @param ixiBot Bot instance.
 */
fun run(consoleListener: ConsoleListener, ixiBot: IxiBot) {
    // Start coroutines
    consoleListener.run()

    // Run the bot
    try {
        ixiBot.init()
        ixiBot.run()
    } catch (ce: ConnectException) {
        log.error("Failed to connect to a required API, exiting", ce)
    } finally {
        ixiBot.close()
        consoleListener.close()
    }
}
