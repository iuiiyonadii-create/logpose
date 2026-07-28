package com.uriel.logpose.thamis.multiagent

import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.compat.core.LogPoseLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * FASE 27.4 — THAMIS LAB SELF-IMPROVEMENT ENGINE
 * Analiza el historial de los agentes para optimizar el comportamiento del sistema.
 */
object SelfImprovementEngine {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val dao = LogPoseApplication.instance.container.memoryDatabase.agentMemoryDao()

    /**
     * Ejecuta una auditoría de rendimiento basada en la memoria persistente.
     */
    fun performSelfAudit() {
        scope.launch {
            LogPoseLogger.i("SelfImprovement: Iniciando auditoría de optimización...")
            
            val logs = dao.getLogsForAgent("MotoSafety")
            val failures = logs.filter { !it.successful }
            
            if (failures.size > 10) {
                LogPoseLogger.w("SelfImprovement: Detectada alta tasa de bloqueos de seguridad (${failures.size}).")
                proposeOptimization("RELAX_THRESHOLD_IF_STATIONARY")
            }

            val recentDecisions = logs.take(20)
            val successRate = recentDecisions.count { it.successful }.toFloat() / recentDecisions.size.coerceAtLeast(1)
            
            LogPoseLogger.i("SelfImprovement: Tasa de éxito reciente: ${successRate * 100}%")
            
            if (successRate < 0.5f) {
                CollaborationBus.postMessage("SelfImprovement", "CRITICAL: Baja eficiencia detectada. Solicitando revisión de NLU.")
            }
        }
    }

    private fun proposeOptimization(strategy: String) {
        LogPoseLogger.i("SelfImprovement: Proponiendo nueva estrategia: $strategy")
        CollaborationBus.postMessage("SelfImprovement", "OPTIMIZATION_PROPOSAL: $strategy")
        com.uriel.logpose.core.network.PCBridge.sendCommand("AI_STRATEGY:$strategy")
    }
}
