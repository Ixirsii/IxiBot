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

package com.ixibot

import com.google.common.annotations.VisibleForTesting
import com.google.inject.Guice
import com.google.inject.Injector
import com.ixibot.data.BotConfiguration
import com.ixibot.module.IxiBotModule
import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.net.ConnectException

/**
 * Main class.
 *
 * @author Ryan Porterfield
 */
class Main(
    /**
     * Guava injector.
     */
    private val injector: Injector
) {

    /**
     * Run bot.
     */
    @VisibleForTesting
    fun  /* default */ run() {
        try {
            injector.getInstance(IxiBot::class.java).use { ixiBot ->
                ixiBot.init()
                ixiBot.run()
            }
        } catch (ce: ConnectException) {
            log.error("Failed to connect to a required API, exiting", ce)
        }
    }

    companion object {
        /**
         * Logger.
         */
        private val log: Logger = LoggerFactory.getLogger(Main::class.java)

        /**
         * Main method.
         *
         * @param args Execution arguments.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val injector = Guice.createInjector(IxiBotModule())
            val (_, isDefaultConfig) = injector.getInstance(BotConfiguration::class.java)
            if (isDefaultConfig) {
                val configFile = File(IxiBot.USER_CONFIG_FILE)
                try {
                    generateUserConfig(configFile)
                    log.info("Generated new user config file at \"{}\". "
                            + "Please customize your configuration then restart the bot",
                            configFile.absolutePath)
                } catch (ioe: IOException) {
                    log.error(
                            "Caught IOException trying to write new user config file to \"{}\"",
                            configFile.absolutePath,
                            ioe)
                }
            } else {
                val main = Main(injector)
                main.run()
            }
        }

        /**
         * Write default configuration file and exit.
         *
         * @param configFile File path to user config file.
         * @throws IOException on error writing config file.
         */
        @JvmStatic
        @VisibleForTesting
        /* default */
        @Throws(IOException::class)
        fun generateUserConfig(configFile: File) {
            Main::class.java.getResourceAsStream(IxiBot.CONFIG_RESOURCE).use { configResource ->
                log.debug(
                        "Writing new user config file to \"{}\"",
                        configFile.absolutePath)
                FileUtils.copyToFile(configResource, configFile)
            }
        }
    }
}