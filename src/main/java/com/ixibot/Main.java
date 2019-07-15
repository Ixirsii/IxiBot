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

import com.ixibot.data.BotConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Scanner;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
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
     * File path to bot configuration resource.
     */
    private static final String CONFIG_RESOURCE = "/config.yaml";
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
        final BotConfiguration botConfiguration;

        try (InputStream configResource = Main.class.getResourceAsStream(CONFIG_RESOURCE)) {
            final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory())
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
                    .registerModule(new Jdk8Module());
            botConfiguration = objectMapper.readValue(configResource, BotConfiguration.class);
        } catch (final IOException ioe) {
            log.error("Caught IOException trying to read bot configuration, exiting", ioe);

            return;
        }

        try (IxiBot ixiBot = new IxiBot(botConfiguration)) {
            ixiBot.run();
            final Scanner scanner = new Scanner(System.in, "UTF-8");

            do {
                log.info("Type \"quit\" to exit");
                final String input = scanner.nextLine();
                log.info("Got user input: {}", input);

                if (QUIT_COMMAND.equals(input)) {
                    isRunning = false;
                }
            } while (isRunning);
        } catch (final ClassNotFoundException cnfe) {
            log.error("Caught ClassNotFoundException, exiting", cnfe);
        } catch (final SQLException sqle) {
            log.error("Caught SQLException, exiting", sqle);
        }
    }
}
