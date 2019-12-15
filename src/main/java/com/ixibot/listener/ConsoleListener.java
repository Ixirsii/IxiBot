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

package com.ixibot.listener;

import com.ixibot.event.BotStopEvent;

import java.util.Scanner;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Listen to console input.
 *
 * @author Ryan Porterfield
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class ConsoleListener implements Runnable {
    /**
     * Command to stop execution.
     */
    private static final String QUIT_COMMAND = "quit";

    /**
     * Event bus to publish events to.
     */
    @NonNull
    private final EventBus eventBus;
    /**
     * System input.
     */
    private final Scanner scanner = new Scanner(System.in, "UTF-8");

    /**
     * Program loop control.
     */
    private boolean isRunning;

    /**
     * BotStopEvent subscriber.
     *
     * @param event Event published to the event bus.
     */
    @Subscribe
    public void onStop(@NonNull final BotStopEvent event) {
        isRunning = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        log.info("Type \"quit\" to exit");

        while (isRunning) {
            final String input = scanner.nextLine();
            log.debug("Got user input: {}", input);

            if (QUIT_COMMAND.equals(input)) {
                final BotStopEvent event = new BotStopEvent(true);

                eventBus.post(event);
            }
        }
    }
}
