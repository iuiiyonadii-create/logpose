package com.uriel.logpose.thamis.trust.validation

import com.uriel.logpose.thamis.trust.monitor.TrustEngine

/**
 * Suite de simulación para validar la capacidad explicativa bajo carga.
 */
class TrustStressTest {

    fun runScenario() {
        // Simular múltiples pedidos de explicación
        repeat(20) { i ->
            TrustEngine.requestExplanation("REJECTED", "Riesgo alto detectado en ciclo $i")
        }
    }
}
