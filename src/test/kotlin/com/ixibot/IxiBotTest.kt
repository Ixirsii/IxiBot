package com.ixibot

import arrow.core.none
import arrow.core.some
import com.ixibot.data.BotConfiguration
import com.ixibot.database.Database
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockkClass
import io.mockk.mockkStatic
import io.mockk.verifySequence
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import org.koin.test.junit5.mock.MockProviderExtension
import org.koin.test.mock.declare
import org.koin.test.mock.declareMock
import java.io.File
import java.io.IOException
import java.sql.SQLException
import kotlin.test.assertTrue

@ExtendWith(MockKExtension::class)
@MockKExtension.ConfirmVerification
@MockKExtension.CheckUnnecessaryStub
class IxiBotTest: KoinTest {
    @MockK
    private lateinit var botConfiguration: BotConfiguration
    @MockK
    private lateinit var configFileMock: File

    private val configFile: File by inject()
    private val database: Database by inject()

    private val underTest: IxiBot = IxiBot()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                single { botConfiguration.some() }
                single { File(CONFIG_DIRECTORY + CONFIG_FILE_NAME) }
                single(named("resourceFilePath")) { CONFIG_FILE_NAME }
            }
        )
    }

    @JvmField
    @RegisterExtension
    val mockProvider = MockProviderExtension.create {
        mockkClass(it)
    }

    @AfterEach
    internal fun tearDown() {
        clearAllMocks()
    }

    @Test
    internal fun `GIVEN success WHEN close THEN closes resources`() {
        // Given
        declareMock<Database> { justRun { close() } }

        // When
        underTest.close()

        // Then
        verifySequence {
            database.close()
        }

    }

    @Test
    internal fun `GIVEN SQLException WHEN close THEN closes resources`() {
        // Given
        declareMock<Database> { every { close() } throws SQLException() }

        // When
        underTest.close()

        // Then
        verifySequence {
            database.close()
        }
    }

    @Test
    internal fun `GIVEN missing bot configuration WHEN init THEN writes config`() {
        // Given
        declare { none<BotConfiguration>() }

        // When
        underTest.init()

        // Then
        assertTrue("Config file should exist") { configFile.exists() }

        configFile.delete()
    }

    @Test
    internal fun `GIVEN IllegalArgumentException WHEN init THEN does nothing`() {
        // Given
        declare { none<BotConfiguration>() }
        declare(named("resourceFilePath")) { "invalid_file_name.yaml" }

        // When
        underTest.init()
    }

    @Test
    internal fun `GIVEN IOException WHEN init THEN does nothing`() {
        // Given
        declare { none<BotConfiguration>() }
        declare { configFileMock }

        // Workaround for mocking writeBytes
        mockkStatic(File::writeBytes)

        every { configFileMock.parentFile } returns configFileMock
        every { configFileMock.exists() } returns true
        every { configFileMock.writeBytes(any()) } throws IOException()
        every { configFileMock.absolutePath } returns "/config.yaml"

        // When
        underTest.init()

        // Then
        verifySequence {
            configFileMock.parentFile
            configFileMock.exists()
            configFileMock.writeBytes(any())
            configFileMock.absolutePath
        }
    }
}
