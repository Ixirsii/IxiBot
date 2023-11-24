package com.ixibot

import arrow.core.Option
import com.google.common.eventbus.EventBus
import com.google.common.io.Resources
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
import com.ixibot.module.userConfigFile
import com.ixibot.module.yamlMapper
import com.ixibot.subscriber.DatabaseSubscriber
import com.ixibot.subscriber.DiscordSubscriber
import discord4j.core.GatewayDiscordClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

/**
 * Logger.
 */
private val log: Logger = LoggerFactory.getLogger(IxiBot::class.java)

/**
 * Main method.
 */
fun main() {
    val configFile = userConfigFile()
    val configurationOption: Option<BotConfiguration> = botConfiguration(configFile, yamlMapper())

    configurationOption.onNone { generateUserConfig(configFile) }
        .onSome {
            // TODO: Move some of these into modules
            val database: Database = database(connection())
            val discordClient: GatewayDiscordClient = discordClient(it)
            val eventBus = EventBus()
            val ixiBot: IxiBot = ixiBot(database, DiscordAPI(discordClient))
            val consoleListener = ConsoleListener(eventBus, CoroutineScope(Dispatchers.Default).coroutineContext)
            val discordListener = DiscordListener(discordClient.eventDispatcher, eventBus)

            listOf<Any>(DatabaseSubscriber(database), DiscordSubscriber(database))
                .forEach(eventBus::register)

            try {
                run(consoleListener, ixiBot)
            } finally {
                ixiBot.close()
                consoleListener.close()
            }
        }
}

/**
 * Write default configuration file and exit.
 *
 * @param configFile File path to user config file.
 */
private fun generateUserConfig(configFile: File) {
    try {
        configFile.writeBytes(Resources.toByteArray(Resources.getResource(CONFIG_FILE_NAME)))

        log.info(
            "Generated new user config file at \"{}\". Please customize your configuration then restart the bot",
            configFile.absolutePath
        )
    } catch (ex: IllegalArgumentException) {
        log.error("Failed to get resource {}", CONFIG_FILE_NAME, ex)
    } catch (ex: IOException) {
        log.error(
            "Encountered exception while trying to write new user config file to \"{}\"",
            configFile.absolutePath,
            ex
        )
    }
}

/**
 * Run bot.
 *
 * Initialization and cleanup of parameters is done here.
 *
 * @param consoleListener Coroutine which listens for console input.
 * @param ixiBot Bot instance.
 */
private fun run(consoleListener: ConsoleListener, ixiBot: IxiBot) {
    // Start coroutines
    consoleListener.run()

    // Run the bot
    ixiBot.init()
    // ixiBot.run()
}
