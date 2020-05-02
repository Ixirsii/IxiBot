package com.ixibot;

import java.io.File;
import java.net.ConnectException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ixibot.MainKt.generateUserConfig;
import static com.ixibot.MainKt.run;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MainTest {
    @Mock
    private IxiBot ixiBotMock;

    @AfterEach
    void cleanup() {
        verifyNoMoreInteractions(ixiBotMock);
    }

    @Test
    void GIVEN_successfulInit_WHEN_start_THEN_runsAndCloses() throws Exception {
        when(injectorMock.getInstance(IxiBot.class)).thenReturn(ixiBotMock);

        try {
            run();
        } finally {
            verify(injectorMock).getInstance(IxiBot.class);
            verify(ixiBotMock).init();
            verify(ixiBotMock).run();
            verify(ixiBotMock).close();
        }
    }

    @Test
    void GIVEN_connectionException_WHEN_start_THEN_exits() throws Exception {
        when(injectorMock.getInstance(IxiBot.class)).thenReturn(ixiBotMock);
        doThrow(new ConnectException()).when(ixiBotMock).init();

        try {
            run();
        } finally {
            verify(injectorMock).getInstance(IxiBot.class);
            verify(ixiBotMock).init();
            verify(ixiBotMock).close();
        }
    }

    @Test
    void GIVEN_validPath_WHEN_generateUserConfig_THEN_writesConfig(@TempDir final File tempFile)
            throws Exception {
        final File configFile = new File(tempFile, "config.yaml");

        generateUserConfig(configFile);

        assertTrue(configFile.exists(), "Config file should be written successfully");
    }
}
