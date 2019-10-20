package com.ixibot.module;

import com.ixibot.data.BotConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ixibot.util.TestData.YAML_MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BotConfigurationModuleTest {
    private static final String COMMAND_PREFIX = "./";
    private static final String DISCORD_TOKEN = "discordToken";
    private static final long ROLE_VERIFY_DELAY = 10L;

    private static final String INVALID_CONFIGURATION = String.format(
            "commandPrefix: %s%ndiscordToken: %s",
            COMMAND_PREFIX,
            DISCORD_TOKEN);
    private static final String VALID_CONFIGURATION = String.format(
            "commandPrefix: %s%ndiscordToken: %s%nroleVerifyDelay: %d",
            COMMAND_PREFIX,
            DISCORD_TOKEN,
            ROLE_VERIFY_DELAY);

    private final BotConfigurationModule underTest = new BotConfigurationModule();

    @Test
    void givenValidUserConfiguration_whenBotConfiguration_thenReturnsBotConfig(
            @TempDir final File tempFile) throws Exception {
        final File tempUserConfigFile = new File(tempFile, "config.yaml");
        final BotConfiguration expected = BotConfiguration.builder()
                .commandPrefix(COMMAND_PREFIX)
                .discordToken(DISCORD_TOKEN)
                .roleVerifyDelay(ROLE_VERIFY_DELAY)
                .build();

        FileUtils.write(tempUserConfigFile, VALID_CONFIGURATION, Charset.defaultCharset());

        final BotConfiguration actual = underTest.botConfiguration(tempUserConfigFile, YAML_MAPPER);

        assertEquals(expected, actual, "Bot config should equal user config");
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
        final BotConfiguration expected = BotConfiguration.builder()
                .commandPrefix(COMMAND_PREFIX)
                .defaultConfig(true)
                .discordToken(DISCORD_TOKEN)
                .roleVerifyDelay(ROLE_VERIFY_DELAY)
                .build();

        final BotConfiguration actual = underTest.botConfiguration(tempUserConfigFile, YAML_MAPPER);

        assertEquals(expected, actual, "Bot config should equal default config");
    }

    @Test
    void userConfigFile() {
        final File expected = new File("config/config.yaml");
        final File actual = underTest.userConfigFile();

        assertEquals(expected, actual, "User config file should equal expected");
    }
}
