package com.uriel.logpose.thamis.beta.monitor

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.beta.feedback.FeedbackCollector
import com.uriel.logpose.thamis.beta.model.DrivingScenario
import com.uriel.logpose.thamis.beta.safety.SafetyAnalyzer
import com.uriel.logpose.thamis.beta.session.DrivingSessionManager

/**
 * Motor principal de la beta cerrada de LogPose.
 */
object BetaTestingEngine {

    fun startTripValidation(scenario: DrivingScenario) {
        LogPoseLogger.i("THAMIS_BETA: Iniciando validación real en escenario $scenario")
        DrivingSessionManager.startSession(scenario)
    }

    fun finishTripValidation() {
        val session = DrivingSessionManager.getActiveSession() ?: return
        val safety = SafetyAnalyzer.analyze(session)
        
        DrivingSessionManager.endSession("Validación finalizada con safety score: ${safety.safetyScore}")
        
        LogPoseLogger.i("THAMIS_BETA: Sesión ${session.id} completada. Ayuda promedio: ${FeedbackCollector.getAverageHelpfulness()}")
    }
}
