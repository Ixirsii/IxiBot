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
import com.ixibot.module.IxiBotModule;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

/**
 * Main class.
 *
 * @author Ryan Porterfield
 */
@RequiredArgsConstructor
@Slf4j
public final class Main {
    /**
     * Guava injector.
     */
    private final Injector injector;

    /**
     * Main method.
     *
     * @param args Execution arguments.
     */
    public static void main(@NonNull final String[] args) {
        final Injector injector = Guice.createInjector(new IxiBotModule());

        final BotConfiguration botConfiguration = injector.getInstance(BotConfiguration.class);

        if (botConfiguration.isDefaultConfig()) {
            final File configFile = new File(IxiBot.USER_CONFIG_FILE);

            try {
                generateUserConfig(configFile);
                log.info("Generated new user config file at \"{}\". "
                                + "Please customize your configuration then restart the bot",
                        configFile.getAbsolutePath());
            }  catch (final IOException ioe) {
                log.error(
                        "Caught IOException trying to write new user config file to \"{}\"",
                        configFile.getAbsolutePath(),
                        ioe);
            }
        } else {
            final Main main = new Main(injector);

            main.run();
        }
    }

    /**
     * Write default configuration file and exit.
     *
     * @param configFile File path to user config file.
     * @throws IOException on error writing config file.
     */
    @VisibleForTesting
    /* default */ static void generateUserConfig(@NonNull final File configFile)
            throws IOException {
        try (InputStream configResource = Main.class.getResourceAsStream(IxiBot.CONFIG_RESOURCE)) {
            log.debug(
                    "Writing new user config file to \"{}\"",
                    configFile.getAbsolutePath());
            FileUtils.copyToFile(configResource, configFile);
        }
    }

    /**
     * Run bot.
     */
    @VisibleForTesting
    /* default */ void run() {
        final IxiBot ixiBot = injector.getInstance(IxiBot.class);

        // TODO: Re-evaluate program flow
        try {
            ixiBot.init();
            ixiBot.run();
        } catch (final ConnectException ce) {
            log.error("Failed to connect to a required API, exiting", ce);
        }

        ixiBot.close();
    }
}
