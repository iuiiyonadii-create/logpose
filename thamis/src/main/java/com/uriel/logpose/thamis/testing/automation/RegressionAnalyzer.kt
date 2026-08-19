package com.uriel.logpose.thamis.testing.automation

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.testing.model.RegressionComparison

/**
 * Detecta cambios negativos entre la versión actual y la anterior.
 */
object RegressionAnalyzer {

    fun compareWithPrevious(module: String, previousLatency: Long, currentLatency: Long): RegressionComparison {
        val diff = currentLatency - previousLatency
        val pct = if (previousLatency > 0) (diff.toFloat() / previousLatency) * 100 else 0f
        
        if (pct > 15.0) {
            LogPoseLogger.e("THAMIS_REGRESSION: Alerta! $module es un ${"%.2f".format(pct)}% más lento.")
        }

        return RegressionComparison(previousLatency, currentLatency, pct, module)
    }
}
