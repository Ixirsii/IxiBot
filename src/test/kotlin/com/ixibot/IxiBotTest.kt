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
    private val underTest: IxiBot = IxiBot(databaseMock, discordAPIMock)

    @Test
    fun `GIVEN success WHEN close THEN closes resources`() {
        underTest.close()

        verifySequence {
            databaseMock.close()
        }

    }

    @Test
    fun `GIVEN SQLE WHEN close THEN closes resources`() {
        every { databaseMock.close() } throws SQLException()

        underTest.close()

        verifySequence {
            databaseMock.close()
        }
    }

    @Test
    fun `GIVEN successful discord connection WHEN init THEN initializes`() {
        underTest.init()
    }
}
