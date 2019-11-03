package com.ixibot;

import com.ixibot.data.BotConfiguration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import com.google.inject.Injector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ixibot.util.TestData.DEFAULT_CONFIG;
import static com.ixibot.util.TestData.USER_CONFIG;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MainTest {
    @Mock
    private Injector injectorMock;
    @Mock
    private IxiBot ixiBotMock;
    @InjectMocks
    private Main underTest;

    @AfterEach
    void cleanup() {
        verify(injectorMock).getInstance(BotConfiguration.class);
        verifyNoMoreInteractions(injectorMock);
        verifyNoMoreInteractions(ixiBotMock);
    }

    @Test
    void GIVEN_defaultConfig_WHEN_start_THEN_writesUserConfig(@TempDir final File tempFile) {
        final File tempUserFile = new File(tempFile, "config.yaml");

        when(injectorMock.getInstance(BotConfiguration.class)).thenReturn(DEFAULT_CONFIG);

        underTest.start(tempUserFile);

        assertTrue(tempUserFile.exists(), "Should write user config file");
    }

    @Test
    void GIVEN_userConfig_WHEN_start_THEN_runsBot(@TempDir final File tempFile) {
        final String input = "quit";
        final InputStream inputStream = new ByteArrayInputStream(input.getBytes());

        System.setIn(inputStream);
        when(injectorMock.getInstance(BotConfiguration.class)).thenReturn(USER_CONFIG);
        when(injectorMock.getInstance(IxiBot.class)).thenReturn(ixiBotMock);

        underTest.start(tempFile);

        verify(injectorMock).getInstance(IxiBot.class);
        verify(ixiBotMock).run();
        verify(ixiBotMock).close();
    }
}
