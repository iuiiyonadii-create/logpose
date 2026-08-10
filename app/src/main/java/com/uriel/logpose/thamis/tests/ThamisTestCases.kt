package com.uriel.logpose.thamis.tests

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.THAMIS
import com.uriel.logpose.thamis.request.THAMISRequest
import com.uriel.logpose.thamis.decision.Decision

/**
 * Suite de validación interna para THAMIS v1.1.
 */
object ThamisTestCases {

    fun runAll() {
        LogPoseLogger.i("THAMIS_TEST", "--- INICIANDO VALIDACIÓN DE CEREBRO ---")
        
        test("pone duki")
        test("hola como estas")
        test("la que sigue")
        
        LogPoseLogger.i("THAMIS_TEST", "--- VALIDACIÓN FINALIZADA ---")
    }

    private fun test(text: String) {
        val request = THAMISRequest(text = text)
        val decision = THAMIS.process(request)
        
        LogPoseLogger.d("THAMIS_TEST", "Input: '$text' -> Decision: ${decision.intent} (Conf: ${decision.confidence})")
        if (decision.entities.isNotEmpty()) {
            decision.entities.forEach { (k, v) -> LogPoseLogger.d("THAMIS_TEST", "   [Entidad] $k = $v") }
        }
    }
}
