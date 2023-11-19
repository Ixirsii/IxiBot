package com.ixibot.event

/**
 * Event to stop the bot.
 *
 * @author Ixirsii <ixirsii@ixirsii.tech>
 */
data class StopBotEvent(
    /**
     * Is the stop graceful.
     *
     * If `true` the bot will try to gracefully disconnect from APIs and stop all threads
     * before exiting, otherwise the bot will "crash" and stop immediately.
     */
    val isGraceful: Boolean,
)
