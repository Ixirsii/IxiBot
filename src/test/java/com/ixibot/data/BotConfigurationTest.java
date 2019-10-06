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

package com.ixibot.data;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

import static com.ixibot.util.TestData.CONFIG_RESOURCE;
import static com.ixibot.util.TestData.YAML_MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Bot configuration test.
 */
class BotConfigurationTest {

    private BotConfiguration underTest;

    BotConfigurationTest() throws IOException {
        try (InputStream configResource = getClass().getResourceAsStream(CONFIG_RESOURCE)) {
            underTest = YAML_MAPPER.readValue(
                    configResource,
                    BotConfiguration.class);
        }
    }

    @Test
    void loadConfig() {
        assertNotNull(underTest, "Mapped object should not be null");
    }

    @Test
    void commandPrefix() {
        assertEquals(
                "./",
                underTest.getCommandPrefix(),
                "Command prefix should equal expected");
    }

    @Test
    void discordToken() {
        assertEquals("NTkxNDMzMTk0NTIwNDQ0OTQ5.XSHDdA.yiwJMNmYPmI6jx00wcs0dsyChqc",
                underTest.getDiscordToken(),
                "Discord token should equal expected");
    }

    @Test
    void roleVerifyDelay() {
        assertEquals(10L,
                underTest.getRoleVerifyDelay(),
                "Role verification delay should equal expected");
    }
}
