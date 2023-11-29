package com.ixibot

import arrow.core.Option
import com.google.common.eventbus.EventBus
import com.google.common.io.Resources
import com.ixibot.data.BotConfiguration
import com.ixibot.database.Database
import com.ixibot.listener.ConsoleListener
import com.ixibot.listener.DiscordListener
import com.ixibot.logging.Logging
import com.ixibot.logging.LoggingImpl
import com.ixibot.subscriber.DatabaseSubscriber
import com.ixibot.subscriber.DiscordSubscriber
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.ReactionAddEvent
import discord4j.core.event.domain.message.ReactionRemoveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.io.File
import java.io.IOException
import java.sql.SQLException

/**
 * Bot configuration file.
 */
const val CONFIG_FILE_NAME = "config.yaml"

/**
 * Bot configuration directory.
 */
const val CONFIG_DIRECTORY = "config/"

/**
 * Main class.
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
     * Console input listener.
     */
    private val consoleListener: ConsoleListener by inject()

    /**
     * Database interface.
     */
    private val database: Database by inject()

    /**
     * Discord client.
     */
    private val discordClient: GatewayDiscordClient by inject()

    /**
     * Discord event listener.
     */
    private val discordListener: DiscordListener by inject()

    /**
     * Pub/sub event bus.
     */
    private val eventBus: EventBus by inject()

    /**
     * Default config resource file.
     */
    private val resourceFilePath: String by inject(named("resourceFilePath"))

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
     */
    fun init() {
        configurationOption.onNone { generateUserConfig(configFile, resourceFilePath) }
            .onSome {
                discordClient.eventDispatcher.on(ReadyEvent::class.java).subscribe(discordListener::readyListener)
                discordClient.eventDispatcher.on(ReactionAddEvent::class.java)
                    .subscribe(discordListener::reactionAddListener)
                discordClient.eventDispatcher.on(ReactionRemoveEvent::class.java)
                    .subscribe(discordListener::reactionRemoveListener)

                listOf<Any>(DatabaseSubscriber(database), DiscordSubscriber(database)).forEach(eventBus::register)
            }
    }

    fun run() {
        val consoleListenerJob: Job = consoleListener.use { it.run() }

        runBlocking {
            consoleListenerJob.join()
        }
    }

    /* **************************************** Private utility methods ***************************************** */

    /**
     * Write default configuration file and exit.
     *
     * @param configFile File path to user config file.
     */
    private fun generateUserConfig(configFile: File, resourceFile: String) {
        try {
            if (!configFile.parentFile.exists()) {
                configFile.parentFile.mkdirs()
            }

            configFile.writeBytes(Resources.toByteArray(Resources.getResource(resourceFile)))

            log.info(
                "Generated new user config file at \"{}\". Please customize your configuration then restart the bot",
                configFile.absolutePath
            )
        } catch (ex: IllegalArgumentException) {
            log.error("Failed to get resource {}", resourceFile, ex)
        } catch (ex: IOException) {
            log.error(
                "Encountered exception while trying to write new user config file to \"{}\"",
                configFile.absolutePath,
                ex
            )
        }
    }
}
