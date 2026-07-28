package com.uriel.logpose.thamis.hardening.stability

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.hardening.model.StabilityState

/**
 * Gestor de estabilidad del sistema THAMIS.
 */
object StabilityManager {
    private val moduleStates = mutableMapOf<String, StabilityState>()

    fun updateModuleHealth(module: String, healthy: Boolean, recoverySuccess: Boolean = true) {
        val current = moduleStates[module]
        val failureCount = if (!healthy) (current?.failureCount ?: 0) + 1 else (current?.failureCount ?: 0)
        
        val newState = StabilityState(
            module = module,
            healthy = healthy,
            failureCount = failureCount,
            lastRecoverySuccessful = recoverySuccess
        )
        
        moduleStates[module] = newState
        
        if (!healthy) {
            LogPoseLogger.e("THAMIS_STABILITY: Modulo $module reportó inestabilidad. Fallos totales: $failureCount")
        }
    }

    fun getModuleState(module: String): StabilityState? = moduleStates[module]

    fun isSystemStable(): Boolean {
        return moduleStates.values.all { it.healthy && it.failureCount < 3 }
    }
}
