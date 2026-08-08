package com.uriel.logpose.thamis.actuator

import com.uriel.logpose.thamis.cognitive.model.ThamisDecision
import com.uriel.logpose.thamis.navigation.provider.NavigationProviderFactory
import com.uriel.logpose.thamis.navigation.provider.NavigationProviderResult
import com.thamis.lab.core.contracts.intent.Intent

import com.uriel.logpose.thamis.capabilities.CapabilityRegistry
import com.uriel.logpose.thamis.capabilities.CapabilityType
import com.uriel.logpose.thamis.navigation.model.NavigationExecutionResult

/**
 * Puente entre el cerebro cognitivo y el sistema de navegación de Android.
 * Utiliza la abstracción de proveedores para desacoplar el cerebro de apps externas.
 */
class NavigationActuator : CognitiveActionExecutor {
    override fun execute(decision: ThamisDecision) {
        val startTime = System.currentTimeMillis()
        val provider = NavigationProviderFactory.getBestAvailableProvider()
        val destination = decision.winningEvaluation?.hypothesis?.entities?.get("destination") ?: ""

        val result: NavigationProviderResult = when (decision.intent) {
            Intent.GO_HOME -> provider.navigateHome()
            Intent.GO_WORK -> provider.navigateWork()
            Intent.NAVIGATE, Intent.START_ROUTE -> provider.navigate(destination)
            Intent.STOP_NAVIGATION, Intent.CANCEL_ROUTE -> provider.stopNavigation()
            Intent.RESUME_ROUTE -> provider.resumeNavigation()
            else -> NavigationProviderResult(false, provider.getProviderInfo().type, "Intent not supported by actuator")
        }

        val executionTime = System.currentTimeMillis() - startTime

        val executionResult = NavigationExecutionResult(
            provider = result.provider,
            executionTimeMs = executionTime,
            success = result.success,
            reason = result.reason,
            safetyDecision = "APPROVED", // Simplificado
            confidence = decision.winningEvaluation?.finalScore ?: 0f,
            authorityDecision = "GRANTED",
            destination = destination,
            routeIntent = decision.intent
        )

        // Registrar métricas de ejecución
        com.uriel.logpose.core.compat.core.LogPoseLogger.i("[THAMIS_ACTUATOR] Provider=${executionResult.provider} Success=${executionResult.success} Action=${decision.intent}")
        
        // Actualizar Capability Registry
        updateCapabilityRegistry(executionResult)
    }

    private fun updateCapabilityRegistry(result: NavigationExecutionResult) {
        val cap = CapabilityRegistry.getCapability(CapabilityType.NAVIGATION) ?: return
        val newLatency = (cap.averageLatencyMs + result.executionTimeMs) / 2
        val newErrorCount = if (result.success) cap.errorCount else cap.errorCount + 1
        
        CapabilityRegistry.register(cap.copy(
            lastUsedTimestamp = System.currentTimeMillis(),
            averageLatencyMs = newLatency,
            errorCount = newErrorCount,
            healthScore = if (newErrorCount > 10) 0.5f else 1.0f
        ))
        
        com.uriel.logpose.core.compat.core.LogPoseLogger.i("[THAMIS_CAPABILITY] Capability=NAVIGATION Health=${cap.healthScore * 100}% Latency=${newLatency}ms")
    }
}
