package com.uriel.logpose.thamis.activation

import com.uriel.logpose.thamis.cognitive.CognitiveOrchestrator
import com.uriel.logpose.thamis.cognitive.model.WorldState
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.ThamisConfiguration
import com.uriel.logpose.thamis.security.ThamisAuthorityGate
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * Suite de validación para la Fase 13: ACTIVACIÓN PARCIAL CONTROLADA.
 */
class ThamisActivationTest {

    @Before
    fun setup() {
        ThamisConfiguration.authorityEnabled = true
        ThamisConfiguration.shadowMode = false
        // Asegurar que el logger esté inyectado si el orchestrator lo usa
    }

    @Test
    fun testMusicPlayAuthority() {
        println("\nTHAMIS_ACTIVATION_TEST: Música - Poné Duki")
        val worldState = WorldState(
            driving = WorldState.DrivingState(isMoving = true),
            system = WorldState.SystemState(isMusicPlaying = true),
            external = WorldState.ExternalState()
        )
        
        val decision = CognitiveOrchestrator.process("poné duki", Intent.PLAY_MUSIC, worldState)
        
        assertEquals(Intent.PLAY_MUSIC, decision.intent)
        assertTrue("Debe tener confianza alta para música con contexto", decision.winningEvaluation!!.finalScore > 0.65f)
    }

    @Test
    fun testCallAuthorityBlocked() {
        println("\nTHAMIS_ACTIVATION_TEST: Llamadas - Bloqueado por Política")
        val worldState = WorldState(
            driving = WorldState.DrivingState(isMoving = true),
            system = WorldState.SystemState(isMusicPlaying = false),
            external = WorldState.ExternalState()
        )
        
        val decision = CognitiveOrchestrator.process("llamá a juan", Intent.CALL_CONTACT, worldState)
        
        // La decisión cognitiva puede ser CALL_CONTACT, pero ThamisAuthorityGate debe devolver false para canExecute
        assertFalse("Dominio comunicación debe estar bloqueado", ThamisAuthorityGate.canExecute(ThamisAuthorityGate.Domain.COMMUNICATION))
    }

    @Test
    fun testVolumeAuthority() {
        println("\nTHAMIS_ACTIVATION_TEST: Volumen - Subilo")
        val worldState = WorldState(
            driving = WorldState.DrivingState(isMoving = true, speedKmh = 100),
            system = WorldState.SystemState(isMusicPlaying = true),
            external = WorldState.ExternalState()
        )
        
        val decision = CognitiveOrchestrator.process("subí volumen", Intent.SET_VOLUME, worldState)
        assertTrue("Confianza para volumen debe ser suficiente (>0.50)", decision.winningEvaluation!!.finalScore > 0.50f)
    }
}
