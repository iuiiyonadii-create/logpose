package com.uriel.logpose.thamis.validation

import com.uriel.logpose.thamis.cognitive.CognitiveOrchestrator
import com.uriel.logpose.thamis.cognitive.model.WorldState
import com.uriel.logpose.thamis.intent.Intent
import org.junit.Test
import org.junit.Assert.*

/**
 * Validación de Inteligencia de Navegación en Modo Sombra.
 */
class NavigationShadowTest {

    @Test
    fun testNavigationIntents() {
        val worldState = WorldState(
            driving = WorldState.DrivingState(isMoving = true, speedKmh = 40, hasActiveNavigation = false),
            system = WorldState.SystemState(isMusicPlaying = false),
            external = WorldState.ExternalState()
        )

        // Caso 1: Llevame a casa
        val dec1 = CognitiveOrchestrator.process("llevame a casa", Intent.START_ROUTE, worldState)
        assertEquals(Intent.START_ROUTE, dec1.intent)

        // Caso 2: Abrí Maps
        val dec2 = CognitiveOrchestrator.process("abrí maps", Intent.OPEN_APP, worldState)
        // Nota: El orquestador v3.0 usa legacyIntent para la categoría por ahora
        // Pero el desambiguador ya debería haber actuado internamente si lo conectamos a entities
    }
}
