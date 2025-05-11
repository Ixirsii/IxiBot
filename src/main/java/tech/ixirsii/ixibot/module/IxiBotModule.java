package tech.ixirsii.ixibot.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import tech.ixirsii.ixibot.data.Configuration;

import java.io.File;
import java.io.IOException;

/**
 * Main bot module.
 *
 * @author Ryan Porterfield
 * @since 1.0.0
 */
public class IxiBotModule extends AbstractModule {

    @NonNull
    @Provides
    @Singleton
    File configFile(@Named("configFilePath") @NonNull final String configFilePath) {
        return new File(configFilePath);
    }

    @Named("configFileName")
    @NonNull
    @Provides
    @Singleton
    String configFileName() {
        return "config.yml";
    }

    @Named("configFilePath")
    @NonNull
    @Provides
    @Singleton
    String configFilePath(@Named("configFileName") @NonNull final String configFileName) {
        return "config/" + configFileName;
    }

    @Provides
    @Singleton
    Configuration configuration(@NonNull final File configFile, @NonNull final ObjectMapper objectMapper)
            throws IOException {
        if (configFile.exists()) {
            return objectMapper.readValue(configFile, Configuration.class);
        }

        return null;
    }

    @NonNull
    @Provides
    @Singleton
    ObjectMapper objectMapper() {
        return new ObjectMapper(new YAMLFactory())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .registerModule(new JavaTimeModule())
                .registerModule(new Jdk8Module())
                .registerModule(new ParameterNamesModule());
    }
}
