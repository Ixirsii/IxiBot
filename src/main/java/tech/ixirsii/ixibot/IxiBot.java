package tech.ixirsii.ixibot;

import com.google.common.io.Resources;
import com.google.inject.Guice;
import com.google.inject.Injector;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.ixirsii.ixibot.data.Configuration;
import tech.ixirsii.ixibot.module.IxiBotModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * IxiBot main class.
 *
 * @author Ryan Porterfield
 * @since 1.0.0
 */
@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
@Slf4j
public class IxiBot implements AutoCloseable, Runnable {
    /**
     * Configuration file.
     */
    @NonNull
    private final File configFile;
    /**
     * Bot configuration.
     */
    private final Configuration configuration;
    /**
     * Configuration resource file path.
     */
    @Named("configFileName")
    @NonNull
    private final String resourceFilePath;

    /* ********************************************************************************************************** *
     *                                               Static methods                                               *
     * ********************************************************************************************************** */

    /**
     * Main method.
     *
     * @param args Program arguments.
     */
    public static void main(final String[] args) {
        final Injector injector = Guice.createInjector(new IxiBotModule());

        try (IxiBot bot = injector.getInstance(IxiBot.class)) {
            bot.init();
        }
    }

    /* ********************************************************************************************************** *
     *                                              Override methods                                              *
     * ********************************************************************************************************** */

    @Override
    public void close() {

    }

    @Override
    public void run() {
        log.info("Running IxiBot");
    }

    /* ********************************************************************************************************** *
     *                                               Public methods                                               *
     * ********************************************************************************************************** */

    /**
     * Initialize the bot.
     */
    public void init() {
        log.info("Starting IxiBot");

        if (configuration == null) {
            createConfigFile();
        }
    }

    /* ********************************************************************************************************** *
     *                                              Private methods                                               *
     * ********************************************************************************************************** */

    private void createConfigFile() {
        if (!configFile.getParentFile().exists() && !configFile.getParentFile().mkdirs()) {
            log.error("Failed to create config file directory at \"{}\"", configFile.getParentFile().getAbsolutePath());

            return;
        }

        try {
            final Path configFilePath = Paths.get(configFile.getAbsolutePath());

            Files.copy(Resources.getResource(resourceFilePath).openStream(), configFilePath);
            log.info(
                    "Generated new config file at \"{}\". Please customize your configuration then restart the bot",
                    configFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to create config file at \"{}\"", configFile.getAbsolutePath(), e);
        }
    }
}
