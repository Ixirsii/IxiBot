package com.ixibot.listener

import com.google.common.eventbus.EventBus
import com.ixibot.logging.Logging
import com.ixibot.logging.LoggingImpl
import com.ixibot.event.StopBotEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Command to stop execution.
 */
private const val QUIT_COMMAND = "quit"

/**
 * Listen to console input.
 *
 * @author Ixirsii <ixirsii@ixirsii.tech>
 */
class ConsoleListener(
    /** Event bus to publish events to. */
    private val eventBus: EventBus,
    override val coroutineContext: CoroutineContext,
) : AutoCloseable, CoroutineScope, Logging by LoggingImpl<ConsoleListener>() {

    override fun close() {
        cancel()
    }

    /**
     * Launch async process to listen for console input.
     */
    fun run(): Job = launch {
        log.info("Type \"quit\" to exit")
        while (isActive) {
            val input: String = readln()
            log.debug("Got user input: {}", input)
            // TODO: Map of command -> dispatcher instead of if statements
            if (QUIT_COMMAND == input) {
                eventBus.post(StopBotEvent(true))
            }
        }
    }
}
