package com.ixibot.data;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Bot configuration test.
 */
class BotConfigurationTest {
    /**
     * Config file resource URL.
     */
    private static final String CONFIG_RESOURCE = "/config.yaml";

    /**
     * YAML object mapper.
     */
    private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
            .registerModule(new Jdk8Module());

    private BotConfiguration botConfiguration;

    @BeforeEach
    public void init() throws IOException {
        try (InputStream configResource = getClass().getResourceAsStream(CONFIG_RESOURCE)) {
            botConfiguration = objectMapper.readValue(
                    configResource,
                    BotConfiguration.class);
        }
    }

    @Test
    public void loadConfig() {
        assertNotNull(botConfiguration, "Mapped object should not be null");
    }

    @Test
    public void discordToken() {
        assertEquals("my.discord.token",
                botConfiguration.getDiscordToken(),
                "Discord token should equal expected");
    }

    @Test
    public void roleVerifyDelay() {
        assertEquals(10L,
                botConfiguration.getRoleVerifyDelay(),
                "Role verification delay should equal expected");
    }
}
