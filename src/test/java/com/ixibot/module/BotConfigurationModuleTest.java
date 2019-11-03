package com.ixibot.module;

import com.ixibot.data.BotConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static com.ixibot.util.TestData.DEFAULT_CONFIG;
import static com.ixibot.util.TestData.INVALID_CONFIGURATION;
import static com.ixibot.util.TestData.USER_CONFIG;
import static com.ixibot.util.TestData.VALID_CONFIGURATION;
import static com.ixibot.util.TestData.YAML_MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BotConfigurationModuleTest {
    private final BotConfigurationModule underTest = new BotConfigurationModule();

    @Test
    void givenValidUserConfiguration_whenBotConfiguration_thenReturnsBotConfig(
            @TempDir final File tempFile) throws Exception {
        final File tempUserConfigFile = new File(tempFile, "config.yaml");

        FileUtils.write(tempUserConfigFile, VALID_CONFIGURATION, Charset.defaultCharset());

        final BotConfiguration actual = underTest.botConfiguration(tempUserConfigFile, YAML_MAPPER);

        assertEquals(USER_CONFIG, actual, "Bot config should equal user config");
    }

    @Test
    void givenInvalidUserConfiguration_whenBotConfiguration_thenReturnsBotConfig(
            @TempDir final File tempFile) throws Exception {
        final File tempUserConfigFile = new File(tempFile, "config.yaml");

        FileUtils.write(tempUserConfigFile, INVALID_CONFIGURATION, Charset.defaultCharset());

        assertThrows(
                IOException.class,
                () -> underTest.botConfiguration(tempUserConfigFile, YAML_MAPPER),
                "Should throw IOException when config file is invalid");
    }

    @Test
    void givenUserConfigDoesntExist_whenBotConfiguration_thenReturnsDefaultConfig(
            @TempDir final File tempFile) throws Exception {
        final File tempUserConfigFile = new File(tempFile, "config.yaml");

        final BotConfiguration actual = underTest.botConfiguration(tempUserConfigFile, YAML_MAPPER);

        assertEquals(DEFAULT_CONFIG, actual, "Bot config should equal default config");
    }

    @Test
    void userConfigFile() {
        final File expected = new File("config/config.yaml");
        final File actual = underTest.userConfigFile();

        assertEquals(expected, actual, "User config file should equal expected");
    }
}
