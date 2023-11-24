package com.ixibot.data

import com.google.common.io.Resources
import com.ixibot.CONFIG_FILE_NAME
import com.ixibot.module.yamlMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BotConfigurationTest {
    private val underTest: BotConfiguration

    init {
        val json = String(Resources.toByteArray(Resources.getResource(CONFIG_FILE_NAME)))
        underTest = yamlMapper().readValue(json, BotConfiguration::class.java)
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
}
