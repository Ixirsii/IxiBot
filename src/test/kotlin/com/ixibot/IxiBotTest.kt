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

import com.ixibot.api.DiscordAPI
import com.ixibot.database.Database
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.jupiter.api.Test
import java.sql.SQLException
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class IxiBotTest {
    private val databaseMock: Database = mockk(relaxed = true, relaxUnitFun = true)
    private val discordAPIMock: DiscordAPI = mockk(relaxed = true, relaxUnitFun = true)
    private val schedulerMock: ScheduledExecutorService = mockk(relaxed = true, relaxUnitFun = true)
    private val underTest: IxiBot = IxiBot(databaseMock, discordAPIMock, 10L, schedulerMock)

    @Test
    fun `GIVEN success WHEN close THEN closes resources`() {
        every { schedulerMock.awaitTermination(any(), any()) } returns true

        underTest.close()

        verifySequence {
            schedulerMock.shutdown()
            schedulerMock.awaitTermination(30, TimeUnit.SECONDS)
            databaseMock.close()
        }

    }

    @Test
    fun `GIVEN SQLE WHEN close THEN closes resources`() {
        every { schedulerMock.awaitTermination(any(), any()) } returns true
        every { databaseMock.close() } throws SQLException()

        underTest.close()

        verifySequence {
            schedulerMock.shutdown()
            schedulerMock.awaitTermination(30, TimeUnit.SECONDS)
            databaseMock.close()
        }
    }

    @Test
    fun `GIVEN successful discord connection WHEN init THEN initializes`() {
        underTest.init()
    }
}
