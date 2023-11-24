package com.ixibot.listener

import com.google.common.eventbus.EventBus
import com.ixibot.event.StopBotEvent
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verifySequence
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.InputStream

class ConsoleListenerTest {
    private val eventBusMock: EventBus = mockk(relaxed = true, relaxUnitFun = true)
    private val underTest: ConsoleListener = ConsoleListener(eventBusMock, CoroutineScope(Dispatchers.Default).coroutineContext)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `GIVEN quit WHEN run THEN posts event`() {
        val botStopEvent = StopBotEvent(isGraceful = true)
        // Put command in stdin
        val sysInBackup: InputStream = System.`in`
        val input = ByteArrayInputStream("quit".toByteArray())
        System.setIn(input)

        underTest.run()
        underTest.close()

        verifySequence { eventBusMock.post(botStopEvent) }

        // Restore System.in
        System.setIn(sysInBackup)
    }
}
