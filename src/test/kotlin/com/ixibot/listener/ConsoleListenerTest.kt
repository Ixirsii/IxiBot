package com.ixibot.listener

import com.google.common.eventbus.EventBus
import com.ixibot.event.StopBotEvent
import io.mockk.mockk
import io.mockk.verifySequence
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.InputStream

class ConsoleListenerTest {
    private val eventBus: EventBus = mockk(relaxed = true, relaxUnitFun = true)
    private val underTest: ConsoleListener = ConsoleListener(eventBus, CoroutineScope(Dispatchers.IO).coroutineContext)

    @Test
    fun `GIVEN quit WHEN run THEN posts event`() {
        val stopBotEvent = StopBotEvent(isGraceful = true)
        // Put command in stdin
        val sysInBackup: InputStream = System.`in`
        val input = ByteArrayInputStream("quit".toByteArray())
        System.setIn(input)

        runBlocking {
            val job: Job = underTest.run()
            job.cancelAndJoin()
        }

        verifySequence { eventBus.post(stopBotEvent) }

        // Restore System.in
        System.setIn(sysInBackup)
    }
}
