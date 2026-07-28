package com.uriel.logpose.thamis.disambiguation

import com.uriel.logpose.thamis.cognitive.disambiguation.EntityCandidate
import com.uriel.logpose.thamis.cognitive.disambiguation.EntityDisambiguationEngine
import com.uriel.logpose.thamis.cognitive.model.WorldState
import org.junit.Test
import org.junit.Assert.*

/**
 * THAMIS_STRESS_TEST v1.0
 * Validación del motor de desambiguación en escenarios complejos.
 */
class DisambiguationStressTest {

    @Test
    fun testAmbiguousContacts() {
        println("\nTHAMIS_STRESS_TEST: Caso 1 - Contactos Duplicados")
        val worldState = WorldState(
            driving = WorldState.DrivingState(isMoving = true),
            system = WorldState.SystemState(isMusicPlaying = false),
            external = WorldState.ExternalState()
        )
        
        // Simulación: Entrada "Juan" con múltiples candidatos
        // En v1.0 el motor simula candidatos para "rock". 
        // Para este test, validamos la lógica de decisión.
        val decision = EntityDisambiguationEngine.disambiguate("Juan", worldState)
        
        println("INPUT: Juan")
        println("DECISION: ${decision.selectedEntity?.entityType ?: "NONE"}")
        
        // Si hay ambigüedad (v1 simulada), el motor debería tender a baja confianza o rechazo
        assertTrue("Debe requerir confirmación o tener confianza baja", decision.confidence < 0.95f)
    }

    @Test
    fun testExplicitAppOpening() {
        println("\nTHAMIS_STRESS_TEST: Caso 3 - Maps como Aplicación")
        val worldState = WorldState(
            driving = WorldState.DrivingState(isMoving = false),
            system = WorldState.SystemState(isMusicPlaying = true),
            external = WorldState.ExternalState()
        )
        
        val decision = EntityDisambiguationEngine.disambiguate("abrí maps", worldState)
        
        println("INPUT: abrí maps")
        println("REASON: ${decision.reasoning}")
        
        // En v1.0 el motor prioriza Apps si hay verbos explícitos (lógica en ActionMapper + Engine)
        assertNotNull(decision.selectedEntity)
    }

    @Test
    fun testRockstarAmbiguityWithContext() {
        println("\nTHAMIS_STRESS_TEST: Caso 5 - Rockstar con Música")
        val worldState = WorldState(
            driving = WorldState.DrivingState(isMoving = true, speedKmh = 40),
            system = WorldState.SystemState(isMusicPlaying = true),
            external = WorldState.ExternalState()
        )
        
        val decision = EntityDisambiguationEngine.disambiguate("rockstar", worldState)
        
        println("INPUT: rockstar")
        println("WINNER: ${decision.selectedEntity?.entityType}")
        println("CONFIDENCE: ${decision.confidence}")
        
        assertEquals("Debe ganar la música", EntityCandidate.Type.SONG, decision.selectedEntity?.entityType)
        assertTrue("La confianza debe ser alta por el contexto", decision.confidence > 0.80f)
    }

    @Test
    fun testRockstarWithoutContext() {
        println("\nTHAMIS_STRESS_TEST: Caso 6 - Rockstar sin Contexto")
        val worldState = WorldState(
            driving = WorldState.DrivingState(isMoving = false),
            system = WorldState.SystemState(isMusicPlaying = false),
            external = WorldState.ExternalState()
        )
        
        val decision = EntityDisambiguationEngine.disambiguate("rockstar", worldState)
        
        println("INPUT: rockstar")
        println("CONFIDENCE: ${decision.confidence}")
        
        // Sin contexto musical, la confianza de que sea una canción no debería llegar a EXECUTE (>0.85)
        assertTrue("No debe ejecutar automáticamente sin evidencia", decision.confidence < 0.85f)
    }
}
