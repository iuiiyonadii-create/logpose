package com.uriel.logpose.thamis.safety.validation

import com.uriel.logpose.thamis.safety.monitor.SafetyIntelligenceEngine
import com.uriel.logpose.thamis.safety.model.RidingContext

/**
 * Suite de simulación para validar la inteligencia de seguridad bajo ráfagas de eventos.
 */
class SafetyStressTest {

    fun runScenario() {
        val scenarios = listOf(
            RidingContext(125f, true, false, 5, 0.9f), // Critical
            RidingContext(110f, false, true, 2, 0.6f), // High
            RidingContext(50f, false, false, 1, 0.2f),  // Low
            RidingContext(0f, false, false, 0, 0f)      // Low
        )

        scenarios.forEach { context ->
            SafetyIntelligenceEngine.evaluate(context)
        }
    }
}
