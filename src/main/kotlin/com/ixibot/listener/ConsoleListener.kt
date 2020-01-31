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

package com.ixibot.listener

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.ixibot.event.BotStopEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Listen to console input.
 *
 * @author Ryan Porterfield
 */
class ConsoleListener(
    /**
     * Event bus to publish events to.
     */
    private val eventBus: EventBus
) : Runnable {

    companion object {
        /**
         * Command to stop execution.
         */
        private const val QUIT_COMMAND = "quit"

        private val log: Logger = LoggerFactory.getLogger(ConsoleListener::class.java)
    }

    /**
     * Program loop control.
     */
    private var running: Boolean = true

    /**
     * BotStopEvent subscriber.
     *
     * @param event Event published to the event bus.
     */
    @Subscribe
    fun onStop(event: BotStopEvent) {
        running = false
    }

    /**
     * {@inheritDoc}
     */
    override fun run() {
        log.info("Type \"quit\" to exit")
        while (running) {
            val input: String? = readLine()
            log.debug("Got user input: {}", input)
            // TODO: Map of command -> dispatcher instead of if statements
            if (QUIT_COMMAND == input) {
                val event = BotStopEvent(true)
                eventBus.post(event)
            }
        }
    }
}