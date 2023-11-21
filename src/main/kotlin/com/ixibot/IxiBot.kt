package com.ixibot

import com.ixibot.api.DiscordAPI
import com.ixibot.data.RoleReaction
import com.ixibot.database.Database
import com.ixibot.logging.Logging
import com.ixibot.logging.LoggingImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
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
class IxiBot(
    /** Database interface. */
    private val database: Database,
    /** Discord API interface. */
    private val discordAPI: DiscordAPI,
) : AutoCloseable, Logging by LoggingImpl<IxiBot>() {

    /**
     * `true` while bot is running, `false` when bot is terminating.
     */
    private var running = false

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
}
