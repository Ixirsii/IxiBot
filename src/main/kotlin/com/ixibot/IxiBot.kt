package com.ixibot

import arrow.core.Option
import com.google.common.eventbus.EventBus
import com.google.common.io.Resources
import com.ixibot.api.DiscordAPI
import com.ixibot.data.BotConfiguration
import com.ixibot.data.RoleReaction
import com.ixibot.database.Database
import com.ixibot.listener.ConsoleListener
import com.ixibot.listener.DiscordListener
import com.ixibot.logging.Logging
import com.ixibot.logging.LoggingImpl
import com.ixibot.subscriber.DatabaseSubscriber
import com.ixibot.subscriber.DiscordSubscriber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.io.IOException
import java.net.ConnectException
import java.sql.SQLException
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

/**
 * Bot configuration file.
 */
const val CONFIG_FILE_NAME = "config.yaml"

/**
 * Bot configuration directory.
 */
const val CONFIG_DIRECTORY = "config/"

/**
 * Config file configured by the user.
 */
const val USER_CONFIG_FILE = CONFIG_DIRECTORY + CONFIG_FILE_NAME

/**
 * Bot logic class once startup is complete and user configuration is loaded.
 *
 * @author Ixirsii <ixirsii@ixirsii.tech>
 */
class IxiBot : AutoCloseable, KoinComponent, Logging by LoggingImpl<IxiBot>() {
    /**
     * Bot configuration file.
     */
    private val configFile: File by inject()

    /**
     * Option of BotConfiguration.
     */
    private val configurationOption: Option<BotConfiguration> by inject()

    /**
     * Database interface.
     */
    private val database: Database by inject()

    /**
     * Discord API interface.
     */
    private val discordAPI: DiscordAPI by inject()

    /**
     * Pub/sub event bus.
     */
    private val eventBus: EventBus by inject()

    /**
     * `true` while bot is running, `false` when bot is terminating.
     */
    private var running = false

    init {
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
     * {@inheritDoc}
     */
    override fun close() {
        log.trace("Shutting down bot")
        try {
            database.close()
        } catch (ex: SQLException) {
            log.error("Caught SQLException while attempting to close database", ex)
        }
    }

    /**
     * Initialize bot instance.
     *
     * @throws ConnectException on failure to connect to API.
     */
    @Throws(ConnectException::class)
    fun init() {
        running = true
    }

    /* **************************************** Private utility methods ***************************************** */

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
}
