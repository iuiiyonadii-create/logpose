package com.thamis.lab.core.contracts

import com.thamis.lab.core.contracts.decision.CognitiveDecision
import com.thamis.lab.core.contracts.event.LabEvent
import com.thamis.lab.core.contracts.explanation.CognitiveExplanation
import com.thamis.lab.core.contracts.snapshot.AudioState
import com.thamis.lab.core.contracts.snapshot.BluetoothState
import com.thamis.lab.core.contracts.snapshot.CognitiveSnapshot
import com.thamis.lab.core.contracts.snapshot.LocationState
import com.thamis.lab.core.contracts.version.MessageVersion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContractsTest {

    @Test
    fun testCognitiveSnapshotCreation() {
        val snapshot = CognitiveSnapshot(
            timestampMs = 1000L,
            audioState = AudioState(isPlaying = true, currentMediaApp = "Spotify"),
            bluetoothState = BluetoothState(isConnected = true, isIntercom = true),
            locationState = LocationState(speedKmH = 45.0)
        )

        assertEquals(1000L, snapshot.timestampMs)
        assertTrue(snapshot.audioState.isPlaying)
        assertEquals("Spotify", snapshot.audioState.currentMediaApp)
        assertTrue(snapshot.bluetoothState.isIntercom)
        assertEquals(45.0, snapshot.locationState.speedKmH, 0.01)
    }

    @Test
    fun testCognitiveDecisionAndExplanation() {
        val explanation = CognitiveExplanation(
            reasoningTrace = "High confidence intent match with safety policy pass",
            evidencesUsed = listOf("BT_CONNECTED", "SPEED_45"),
            evaluatedPolicies = listOf("SAFETY_SPEED_CHECK"),
            finalConfidence = 0.95
        )

        val decision = CognitiveDecision(
            intentName = "PLAY_MUSIC",
            confidenceScore = 0.95,
            actionCommand = "MEDIA_PLAY",
            isExecutable = true,
            explanation = explanation
        )

        assertEquals("PLAY_MUSIC", decision.intentName)
        assertTrue(decision.isExecutable)
        assertEquals(2, decision.explanation.evidencesUsed.size)
    }

    @Test
    fun testPolymorphicEvents() {
        val event: LabEvent = LabEvent.TextCommandEvent(
            eventId = "evt-123",
            timestampMs = 2000L,
            userText = "poné música",
            sttConfidence = 0.98
        )

        assertTrue(event is LabEvent.TextCommandEvent)
        val textEvent = event as LabEvent.TextCommandEvent
        assertEquals("poné música", textEvent.userText)
    }

    @Test
    fun testMessageVersionCompatibility() {
        val v1 = MessageVersion(1, 0, 0)
        val v1_1 = MessageVersion(1, 1, 0)
        val v2 = MessageVersion(2, 0, 0)

        assertTrue(v1.isCompatibleWith(v1_1))
        assertTrue(!v1.isCompatibleWith(v2))
    }
}
