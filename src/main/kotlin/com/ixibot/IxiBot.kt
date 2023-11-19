package com.ixibot

import com.ixibot.api.DiscordAPI
import com.ixibot.data.RoleReaction
import com.ixibot.database.Database
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
    /** Interval (in minutes) between Discord role verification checks. */
    private val roleVerifyDelay: Long,
    /**
     * Thread pool executor for scheduled async actions.
     *
     * TODO: Replace this with coroutines
     */
    private val scheduler: ScheduledExecutorService,
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
        shutdownScheduler()
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

    /**
     * Run the bot.
     */
    fun run() {
        scheduler.scheduleAtFixedRate(
            {
                val roleReactions: List<RoleReaction> = database.allRoleReactions
                discordAPI.updateAllRoles(
                    roleReactions.stream()
                        .filter(RoleReaction::isVerified)
                        .collect(Collectors.groupingBy(RoleReaction::guildID))
                )
            },
            0,
            roleVerifyDelay,
            TimeUnit.MINUTES
        )

        // Keep main thread alive
        while (running) {
            runBlocking {
                delay(1000L)
            }
        }
    }

    /**
     * Shutdown thread pool scheduler.
     */
    private fun shutdownScheduler() {
        scheduler.shutdown()
        // Wait for the thread pool to shut down
        try { // Wait for tasks to terminate
            if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) { // (Attempt to) force stop thread pool
                scheduler.shutdownNow()
                // Wait for tasks to respond to being cancelled
                if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                    log.error("Failed to shut down thread pool")
                }
            }
        } catch (ie: InterruptedException) {
            log.error("Thread interrupted while waiting for thread pool to shutdown", ie)
            // Verify that thread pool is shutdown
            scheduler.shutdownNow()
            // Preserve interrupt status
            Thread.currentThread().interrupt()
        }
    }
}
