package com.thamis.lab.headless

import com.thamis.lab.core.contracts.event.LabEvent
import com.thamis.lab.headless.runner.HeadlessRunner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HeadlessRunnerTest {

    @Test
    fun testDeterministicExecutionAndSpeedControl() {
        val runner = HeadlessRunner(masterSeed = 100L)
        val eventList = mutableListOf<LabEvent>()

        runner.eventDispatcher.subscribe { event ->
            eventList.add(event)
        }

        val e1 = LabEvent.TextCommandEvent("evt-1", timestampMs = 100L, userText = "hola")
        val e2 = LabEvent.TextCommandEvent("evt-2", timestampMs = 50L, userText = "chao")

        runner.enqueueEvent(e1)
        runner.enqueueEvent(e2)

        // Event e2 (50ms) should be processed before e1 (100ms)
        val processed1 = runner.step()
        assertNotNull(processed1)
        assertEquals("evt-2", processed1?.eventId)

        val processed2 = runner.step()
        assertNotNull(processed2)
        assertEquals("evt-1", processed2?.eventId)

        assertEquals(2, eventList.size)
        assertEquals("evt-2", eventList[0].eventId)
        assertEquals("evt-1", eventList[1].eventId)
    }

    @Test
    fun testVirtualClockPauseAndResume() {
        val runner = HeadlessRunner()
        runner.clock.pause()
        assertTrue(runner.clock.isPaused)

        runner.enqueueEvent(LabEvent.TextCommandEvent("evt-1", timestampMs = 10L, userText = "test"))
        val result = runner.step()
        assertEquals(null, result)

        runner.clock.resume()
        val resultAfterResume = runner.step()
        assertNotNull(resultAfterResume)
    }

    @Test
    fun testHighThroughputSimulation() {
        val runner = HeadlessRunner()
        val count = 10000
        for (i in 1..count) {
            runner.enqueueEvent(LabEvent.TextCommandEvent("evt-$i", timestampMs = i.toLong(), userText = "cmd $i"))
        }

        val startTime = System.currentTimeMillis()
        val steps = runner.runUntil(count.toLong())
        val duration = System.currentTimeMillis() - startTime

        assertEquals(count, steps)
        assertEquals(count.toLong(), runner.totalProcessedEvents)
        assertTrue("Execution of 10,000 events must take less than 1000ms", duration < 1000)
    }
}
