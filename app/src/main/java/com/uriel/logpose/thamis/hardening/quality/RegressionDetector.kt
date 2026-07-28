package com.uriel.logpose.thamis.hardening.quality

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.hardening.model.RegressionReport

/**
 * Detecta degradaciones en el rendimiento o fiabilidad comparando con estados previos.
 */
object RegressionDetector {

    fun checkRegression(module: String, metric: String, previous: Double, current: Double): RegressionReport {
        val deviation = if (previous != 0.0) ((current - previous) / previous) * 100 else 0.0
        val isRegression = deviation > 15.0 // Tolerancia del 15%

        if (isRegression) {
            LogPoseLogger.w("THAMIS_REGRESSION: Detectada degradación en $module ($metric): ${"%.2f".format(deviation)}%")
        }

        return RegressionReport(module, metric, previous, current, deviation, isRegression)
    }
}
