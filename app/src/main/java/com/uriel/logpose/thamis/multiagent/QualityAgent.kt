package com.uriel.logpose.thamis.multiagent

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.orchestrator.SystemOrchestrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * FASE 27.2 — THAMIS MULTI-AGENT COLLABORATION ENGINE
 * FASE 10: QUALITY AGENT
 */
class QualityAgent : Agent("QualityGate", "Code Quality") {

    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        monitorHealth()
    }

    private fun monitorHealth() {
        scope.launch {
            SystemOrchestrator.systemHealth.collect { health ->
                if (health < 50) {
                    LogPoseLogger.w("QualityAgent: Detectada baja salud del sistema ($health%). Solicitando auto-fix.")
                    CollaborationBus.postMessage("QualityGate", "CRITICAL: System health dropped to $health%. Requesting CodeAgent for patch.")
                    triggerAutoFix()
                }
            }
        }
    }

    private fun triggerAutoFix() {
        // En un caso real, aquí llamaríamos al CodeAgent
        CollaborationBus.postMessage("QualityGate", "ACTION: CodeAgent triggered for system optimization.")
    }

    override fun execute(task: String): String {
        return "Auditoría de calidad para: $task"
    }
}
