package com.uriel.logpose.thamis.navigation

import com.uriel.logpose.thamis.navigation.confidence.ConfidenceDecayEngine
import com.uriel.logpose.thamis.navigation.memory.NavigationExperience
import com.uriel.logpose.thamis.navigation.memory.NavigationMemory
import com.uriel.logpose.thamis.navigation.model.NavigationGoal
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * Suite de pruebas para Inteligencia y Memoria de Navegación.
 */
class NavigationMemoryStressTest {

    @Before
    fun setup() {
        // Reset manual o mock de memoria si fuera necesario
    }

    @Test
    fun testConfidenceDecay() {
        println("\nTEST 4: Decaimiento de confianza temporal")
        val initialConfidence = 0.92f
        
        // Tras 5 segundos
        val decay5s = ConfidenceDecayEngine.calculateDecay(initialConfidence, 5000L)
        assertTrue("La confianza debe bajar tras 5s", decay5s < initialConfidence)
        println("Confianza tras 5s: $decay5s")

        // Tras 21 segundos (Expiración)
        val isExpired = ConfidenceDecayEngine.isExpired(21000L)
        assertTrue("La intención debe expirar tras 21s", isExpired)
        assertEquals(0f, ConfidenceDecayEngine.calculateDecay(initialConfidence, 21000L), 0.01f)
    }

    @Test
    fun testMemoryBoost() {
        println("\nTEST 5: Boost de memoria por destino habitual")
        val destination = "Casa"
        
        // Grabamos 3 experiencias exitosas
        repeat(3) {
            NavigationMemory.recordExperience(NavigationExperience(
                destination = destination,
                goalType = NavigationGoal.GoalType.GO_HOME,
                speedKmh = 0,
                decision = "EXECUTE",
                success = true
            ))
        }

        val boost = NavigationMemory.getMemoryBoost(destination)
        assertTrue("Destino habitual debe recibir boost de confianza", boost > 0.1f)
        println("Boost para Casa: +$boost")
    }

    @Test
    fun testInsufficientInformation() {
        println("\nTEST 2: Llevame (Sin destino)")
        val intent = RouteIntentClassifier.classify("llevame")
        assertEquals(RouteIntentClassifier.RouteIntent.UNKNOWN, intent)
    }

    @Test
    fun testIntentClassification() {
        println("\nTEST 6: Clasificación de intenciones")
        assertEquals(RouteIntentClassifier.RouteIntent.GO_HOME, RouteIntentClassifier.classify("llevame a casa"))
        assertEquals(RouteIntentClassifier.RouteIntent.GO_WORK, RouteIntentClassifier.classify("vamos al trabajo"))
        assertEquals(RouteIntentClassifier.RouteIntent.STOP_ROUTE, RouteIntentClassifier.classify("cancela la ruta"))
    }
}
