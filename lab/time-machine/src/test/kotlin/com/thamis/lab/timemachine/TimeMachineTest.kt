package com.thamis.lab.timemachine

import com.thamis.lab.core.contracts.event.LabEvent
import com.thamis.lab.core.contracts.snapshot.CognitiveSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class TimeMachineTest {

    @Test
    fun testSnapshotRecordingAndRewind() {
        val timeMachine = DeterministicTimeMachine()

        val snap1 = CognitiveSnapshot(timestampMs = 100L, activeApp = "Spotify")
        val snap2 = CognitiveSnapshot(timestampMs = 200L, activeApp = "GoogleMaps")

        timeMachine.recordSnapshot(snap1)
        timeMachine.recordSnapshot(snap2)

        val rewound = timeMachine.rewindTo(150L)
        assertNotNull(rewound)
        assertEquals("Spotify", rewound?.activeApp)
        assertEquals(100L, timeMachine.clock.currentTimeMs)
    }

    @Test
    fun testReplayDeterminism() {
        val timeMachine = DeterministicTimeMachine(masterSeed = 999L)
        val events = listOf(
            LabEvent.TextCommandEvent("evt-1", timestampMs = 10L, userText = "play"),
            LabEvent.TextCommandEvent("evt-2", timestampMs = 20L, userText = "pause")
        )

        val executedCount = timeMachine.replay(events)
        assertEquals(2, executedCount)
        assertEquals(2L, timeMachine.runner.totalProcessedEvents)
    }
}
