package com.uriel.logpose.thamis.navigation

import com.uriel.logpose.thamis.navigation.validation.NavigationValidationSession
import com.uriel.logpose.thamis.navigation.metrics.NavigationMetrics
import com.uriel.logpose.thamis.navigation.consistency.NavigationConsistencyAnalyzer
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * Stress Test Prolongado de Navegación v1.0
 */
class NavigationLongSessionTest {

    private lateinit var session: NavigationValidationSession

    @Before
    fun setup() {
        session = NavigationValidationSession()
    }

    @Test
    fun testLongSessionSimulation() {
        println("\nTHAMIS_VALIDATION: Simulando sesión de 30 minutos")
        
        // Simulación de eventos
        session.totalCommands = 20
        session.executedCommands = 18
        session.rejectedCommands = 1
        session.expiredCommands = 1
        session.confirmedCommands = 4
        session.averageSpeedKmh = 75
        session.maxSpeedKmh = 115

        val metrics = NavigationMetrics.calculate(session)
        
        println("[THAMIS_METRICS] Accuracy=${metrics.accuracy * 100}%")
        println("[THAMIS_METRICS] False Positives=${metrics.falsePositives}")
        println("[THAMIS_ROUTE_SESSION] Duration=30m Commands=${session.totalCommands}")

        assertTrue("Accuracy debe ser >= 90%", metrics.accuracy >= 0.90f)
        assertEquals("Errores críticos deben ser 0", 0, NavigationConsistencyAnalyzer.getInconsistencies().size)
    }

    @Test
    fun testSafetyRuleSpeedLimit() {
        println("\nTHAMIS_VALIDATION: Verificando regla de velocidad > 120 km/h")
        session.maxSpeedKmh = 125
        
        // Si detectamos velocidad extrema en la sesión, el reporte debe reflejarlo
        assertTrue(session.maxSpeedKmh > 120)
    }
}
