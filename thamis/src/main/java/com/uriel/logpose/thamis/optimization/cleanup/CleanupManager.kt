package com.uriel.logpose.thamis.optimization.cleanup

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.optimization.cache.CacheManager
import com.uriel.logpose.thamis.optimization.model.OptimizationPlan
import com.uriel.logpose.thamis.optimization.model.ResourceType

/**
 * Ejecuta limpiezas controladas de recursos temporales.
 */
object CleanupManager {

    fun execute(plan: OptimizationPlan) {
        LogPoseLogger.i("THAMIS_CLEANUP: Iniciando limpieza controlada para ${plan.affectedResource}")
        
        when (plan.affectedResource) {
            ResourceType.CACHE -> CacheManager.cleanup()
            ResourceType.MEMORY -> {
                // Simulación de liberación de memoria interna
                LogPoseLogger.d("THAMIS_CLEANUP: Liberando buffers de historial obsoletos.")
            }
            else -> {}
        }
    }
}
