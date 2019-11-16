package com.ixibot;

import com.ixibot.data.BotConfiguration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.ConnectException;

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
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    void GIVEN_successfulInit_WHEN_start_THEN_returnsTrue(@TempDir final File tempFile)
            throws Exception {
        try {
            final boolean result = Main.start(ixiBotMock);

            assertTrue(result, "Should start successfully");
        } finally {
            verify(ixiBotMock).init();
        }
    }

    @Test
    void GIVEN_connectException_WHEN_start_THEN_returnsFalse(@TempDir final File tempFile)
            throws Exception {
        doThrow(new ConnectException("Test case threw exception")).when(ixiBotMock).init();

        try {
            final boolean result = Main.start(ixiBotMock);

            assertFalse(result, "Should not start successfully");
        } finally {
            verify(ixiBotMock).init();
        }
    }
}
