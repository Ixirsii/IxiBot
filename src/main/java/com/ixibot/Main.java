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

package com.ixibot;

import com.ixibot.module.IxiBotModule;

import java.util.Scanner;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Main class.
 *
 * @author Ryan Porterfield
 */
@Slf4j
public final class Main {
    /**
     * Command to stop execution.
     */
    private static final String QUIT_COMMAND = "quit";

    /**
     * Program loop control.
     */
    private static boolean isRunning = true;

    /**
     * Hide the constructor for utility class.
     */
    private Main() {
    }

    /**
     * Main method.
     *
     * @param args Execution arguments.
     */
    public static void main(@NonNull final String[] args) {
        final Injector injector = Guice.createInjector(new IxiBotModule());
        final IxiBot ixiBot = injector.getInstance(IxiBot.class);
        final Scanner scanner = new Scanner(System.in, "UTF-8");

        ixiBot.run();

        do {
            log.info("Type \"quit\" to exit");
            final String input = scanner.nextLine();
            log.info("Got user input: {}", input);

            if (QUIT_COMMAND.equals(input)) {
                isRunning = false;
            }
        } while (isRunning);
    }
}
