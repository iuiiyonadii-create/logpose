package com.uriel.logpose.thamis.activation

import com.uriel.logpose.thamis.cognitive.CognitiveOrchestrator
import com.uriel.logpose.thamis.cognitive.model.WorldState
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.ThamisConfiguration
import com.uriel.logpose.thamis.actuation.ActuationGateway
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * Validación de Autoridad Musical THAMIS v3.1.
 */
class ThamisMusicAuthorityTest {

    @Before
    fun setup() {
        ThamisConfiguration.authorityEnabled = true
        ThamisConfiguration.shadowMode = false
    }

    @Test
    fun testMusicPlayAuthorized() {
        println("\nTHAMIS_AUTHORITY_TEST: Caso 1 - Poné Rockstar")
        val worldState = WorldState(
            driving = WorldState.DrivingState(isMoving = true, speedKmh = 40),
            system = WorldState.SystemState(isMusicPlaying = true),
            external = WorldState.ExternalState()
        )
        
        val decision = CognitiveOrchestrator.process("poné rockstar", Intent.PLAY_MUSIC, worldState)
        val actuation = ActuationGateway.requestActuation(decision, worldState)
        
        println("[THAMIS_DECISION] Goal=${decision.winningEvaluation?.hypothesis?.candidateGoal?.category} Confidence=${decision.winningEvaluation?.finalScore}")
        println("[THAMIS_AUTHORITY] Permission=${if (actuation.success) "GRANTED" else "DENIED"}")
        
        assertTrue("Debe estar autorizado", actuation.success)
        assertEquals(Intent.PLAY_MUSIC, actuation.action)
    }

    @Test
    fun testNextTrackAuthorized() {
        println("\nTHAMIS_AUTHORITY_TEST: Caso 3 - Siguiente")
        val worldState = WorldState(
            driving = WorldState.DrivingState(isMoving = true),
            system = WorldState.SystemState(isMusicPlaying = true),
            external = WorldState.ExternalState()
        )
        
        val decision = CognitiveOrchestrator.process("siguiente", Intent.NEXT_TRACK, worldState)
        val actuation = ActuationGateway.requestActuation(decision, worldState)
        
        assertTrue("Siguiente debe estar autorizado con música activa", actuation.success)
    }

    @Test
    fun testCommunicationBlocked() {
        println("\nTHAMIS_AUTHORITY_TEST: Caso 5 - Llamá a Juan")
        val worldState = WorldState(
            driving = WorldState.DrivingState(isMoving = true),
            system = WorldState.SystemState(isMusicPlaying = false),
            external = WorldState.ExternalState()
        )
        
        val decision = CognitiveOrchestrator.process("llamá a juan", Intent.CALL_CONTACT, worldState)
        val actuation = ActuationGateway.requestActuation(decision, worldState)
        
        println("[THAMIS_AUTHORITY] Domain=COMMUNICATION Permission=${if (actuation.success) "GRANTED" else "DENIED"}")
        
        assertFalse("Llamadas deben estar bloqueadas por política", actuation.success)
        assertTrue(actuation.reason.contains("bloqueado"))
    }
}
