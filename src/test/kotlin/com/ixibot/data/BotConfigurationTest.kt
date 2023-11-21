/*
 * Copyright (c) 2020, Ryan Porterfield
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *
 *     3. Neither the name of the copyright holder nor the names of its
 *        contributors may be used to endorse or promote products derived from
 *        this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 *  IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *  PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 *  HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.ixibot.data

import com.ixibot.CONFIG_FILE_NAME
import com.ixibot.module.yamlMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BotConfigurationTest {
    private val underTest: BotConfiguration

    init {
        javaClass.classLoader.getResourceAsStream(CONFIG_FILE_NAME).use { configResource ->
            val botConfiguration: BotConfiguration = yamlMapper().readValue(
                    configResource,
                    BotConfiguration::class.java)
            underTest = BotConfiguration(
                    commandPrefix = botConfiguration.commandPrefix,
                    isDefaultConfig = true,
                    isDiscordRequired = botConfiguration.isDiscordRequired,
                    discordToken = botConfiguration.discordToken,
                    roleVerifyDelay = botConfiguration.roleVerifyDelay)
        }
    }

    @Test
    fun `GIVEN valid config file WHEN read user config THEN successfully builds BotConfiguration`() {
        assertNotNull(underTest, "Mapped object should not be null")
    }

    @Test
    fun `GIVEN default value WHEN commandPrefix THEN returns expected`() {
        assertEquals(
                "./",
                underTest.commandPrefix,
                "Command prefix should equal expected")
    }

    @Test
    fun `GIVEN default value WHEN discordToken THEN returns expected`() {
        assertEquals(
                "discordToken",
                underTest.discordToken,
                "Discord token should equal expected")
    }

    @Test
    fun `GIVEN default config WHEN isDefaultConfig THEN returns true`() {
        assertTrue(underTest.isDefaultConfig, "Config object should not be default")
    }

    @Test
    fun `GIVEN default value WHEN isDiscordRequired THEN returns false`() {
        assertTrue(underTest.isDiscordRequired, "Discord should be required")
    }

    @Test
    fun `GIVEN default value WHEN roleVerifyDelay THEN returns expected`() {
        assertEquals(
                10L,
                underTest.roleVerifyDelay,
                "Role verification delay should equal expected")
    }
}
