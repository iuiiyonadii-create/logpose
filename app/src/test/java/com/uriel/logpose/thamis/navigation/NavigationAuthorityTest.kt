package com.uriel.logpose.thamis.navigation

import com.uriel.logpose.thamis.cognitive.CognitiveOrchestrator
import com.uriel.logpose.thamis.cognitive.model.WorldState
import com.uriel.logpose.thamis.intent.Intent
import com.uriel.logpose.thamis.ThamisConfiguration
import com.uriel.logpose.thamis.security.ThamisAuthorityGate
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * Pruebas de autoridad real para navegación THAMIS v3.0.
 */
class NavigationAuthorityTest {

    @Before
    fun setup() {
        ThamisConfiguration.authorityEnabled = true
        ThamisConfiguration.navigationEnabled = true
        ThamisConfiguration.shadowMode = false
    }

    @Test
    fun testNavigateHomeAuthorized() {
        println("\nTHAMIS_NAV_AUTHORITY: Caso 1 - Llevame a casa")
        val worldState = WorldState(
            driving = WorldState.DrivingState(isMoving = true, speedKmh = 40),
            system = WorldState.SystemState(isMusicPlaying = false),
            external = WorldState.ExternalState()
        )
        
        val decision = CognitiveOrchestrator.process("llevame a casa", Intent.GO_HOME, worldState)
        
        assertEquals(Intent.GO_HOME, decision.intent)
        // La validación interna de autoridad ya ocurrió en process()
    }

    @Test
    fun testHighSpeedBlocking() {
        println("\nTHAMIS_NAV_AUTHORITY: Caso - 130 km/h")
        val worldState = WorldState(
            driving = WorldState.DrivingState(isMoving = true, speedKmh = 130),
            system = WorldState.SystemState(isMusicPlaying = false),
            external = WorldState.ExternalState()
        )
        
        val decision = CognitiveOrchestrator.process("llevame a casa", Intent.GO_HOME, worldState)
        
        // El orquestador debió bloquear la ejecución (aunque la intención sea correcta)
        val validation = NavigationAuthorityValidator.validate(decision, worldState)
        assertTrue(validation is NavigationAuthorityValidator.ValidationResult.DENY)
    }
}
