package com.uriel.logpose.thamis.journeyintelligence.validation

import com.uriel.logpose.thamis.journeyintelligence.monitor.JourneyIntelligenceEngine

/**
 * Suite de simulación para validar el reconocimiento de patrones y contextos.
 */
class JourneyStressTest {

    fun runScenario() {
        // Simular múltiples viajes por la misma ruta
        repeat(5) {
            JourneyIntelligenceEngine.analyzeCurrentJourney(5000.0, 600000, "ROUTE_HOME")
        }
        
        // Simular viaje largo desconocido
        JourneyIntelligenceEngine.analyzeCurrentJourney(150000.0, 7200000, "NEW_ROUTE_01")
    }
}
