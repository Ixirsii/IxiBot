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

package com.ixibot

import com.ixibot.listener.ConsoleListener
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kotlinx.coroutines.cancel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.net.ConnectException

@ExtendWith(MockKExtension::class)
class MainTest {
    @MockK(relaxed = true, relaxUnitFun = true)
    private lateinit var consoleListenerMock: ConsoleListener
    @MockK(relaxed = true, relaxUnitFun = true)
    private lateinit var ixiBotMock: IxiBot

    @AfterEach
    fun cleanUp() {
        confirmVerified(consoleListenerMock, ixiBotMock)
    }

    @Test
    fun `GIVEN successful init WHEN start THEN run and close`() {
        try {
            run(consoleListenerMock, ixiBotMock)
        } finally {
            verify { consoleListenerMock.run() }
            verify { ixiBotMock.init() }
            verify { ixiBotMock.run() }
            verify { ixiBotMock.close() }
            verify { consoleListenerMock.close() }
        }
    }

    @Test
    fun `GIVEN ConnectionException WHEN start THEN exits`() {
        every { consoleListenerMock.run() } answers { nothing }
        every { ixiBotMock.init() } throws ConnectException()

        try {
            run(consoleListenerMock, ixiBotMock)
        } finally {
            verify { consoleListenerMock.run() }
            verify { ixiBotMock.init() }
            verify { ixiBotMock.close() }
            verify { consoleListenerMock.close() }
        }
    }

    @Test
    fun `GIVEN valid path WHEN generateUserConfig THEN writes config`(@TempDir tempFile: File) {
        val configFile = File(tempFile, "config.yaml")

        generateUserConfig(configFile)

        assertTrue(configFile.exists(), "Config file should be written successfully")
    }
}