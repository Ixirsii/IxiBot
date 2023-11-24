package com.ixibot

import arrow.core.Option
import com.google.common.io.Resources
import com.ixibot.data.BotConfiguration
import com.ixibot.listener.ConsoleListener
import com.ixibot.listener.DiscordListener
import com.ixibot.module.*
import com.ixibot.subscriber.DatabaseSubscriber
import com.ixibot.subscriber.DiscordSubscriber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.startKoin
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
    startKoin {
        modules(BotModule().module, DatabaseModule().module)
    }

    IxiBot().use {
        it.init()
    }

    val configFile = userConfigFile()
    val configurationOption: Option<BotConfiguration> = botConfiguration(configFile, yamlMapper())

    configurationOption.onNone { generateUserConfig(configFile) }
        .onSome {
            val consoleListener = ConsoleListener(eventBus, CoroutineScope(Dispatchers.Default).coroutineContext)
            val discordListener = DiscordListener(discordClient.eventDispatcher, eventBus)

            listOf<Any>(DatabaseSubscriber(database), DiscordSubscriber(database))
                .forEach(eventBus::register)

            consoleListener.use { it.run() }
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
