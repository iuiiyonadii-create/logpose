package com.uriel.logpose.thamis.improvement.validation

import com.uriel.logpose.thamis.improvement.model.ExperimentResult

/**
 * Valida si una mejora ha cumplido su objetivo sin introducir regresiones.
 */
object ImprovementValidator {

    fun validateExperiment(result: ExperimentResult): Boolean {
        // En v1.0, validamos que la conclusión sea positiva y no haya fallos críticos
        return result.isSuccessful && !result.conclusion.contains("CRITICAL_REGRESSION")
    }
}
