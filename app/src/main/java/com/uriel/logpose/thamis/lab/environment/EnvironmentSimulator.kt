package com.uriel.logpose.thamis.lab.environment

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.lab.model.VoiceScenario

/**
 * Simula condiciones externas como ruido ambiental y clima.
 */
object EnvironmentSimulator {

    fun applyVoiceNoise(scenario: VoiceScenario) {
        LogPoseLogger.i("THAMIS_SIMULATION: Aplicando ruido de ${scenario.noiseType} (Nivel: ${scenario.noiseLevel})")
    }

    fun simulateGpsDrift(intensity: Float) {
        LogPoseLogger.w("THAMIS_SIMULATION: Simulando deriva de GPS. Intensidad: $intensity")
    }
}
