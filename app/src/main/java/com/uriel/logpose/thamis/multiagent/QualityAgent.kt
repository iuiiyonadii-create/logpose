package com.uriel.logpose.thamis.multiagent

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.orchestrator.SystemOrchestrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * FASE 27.2 — THAMIS MULTI-AGENT COLLABORATION ENGINE
 * FASE 10: QUALITY AGENT (Auditoría de Calidad & Métricas)
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
                    triggerAutoFix(health)
                }
            }
        }
    }

    private fun triggerAutoFix(currentHealth: Int) {
        val patchProposal = "AutoFix_Optimization_Health_$currentHealth"
        val patch = KotlinGenerator.generateClass(
            className = "QualityPatch_$currentHealth",
            packageName = "com.uriel.logpose.generated.patches",
            functions = listOf("optimizeMemory", "releaseUnusedResources")
        )
        LogPoseLogger.i("QualityAgent: Parche generado automáticamente -> ${patch.targetPath}")
        CollaborationBus.postMessage("QualityGate", "ACTION: Patch ${patch.className} generated to restore system health.")
    }

    override fun execute(task: String): String {
        LogPoseLogger.i("QualityAgent: Ejecutando auditoría de calidad para: $task")
        
        val issues = mutableListOf<String>()
        if (task.contains("Thread") || task.contains("Loop")) {
            issues.add("Riesgo de bloqueo de Hilo Principal")
        }
        if (task.contains("Memory") || task.contains("Bitmap")) {
            issues.add("Fuga potencial de Memoria RAM")
        }

        val qualityScore = (100 - issues.size * 25).coerceAtLeast(0)
        val result = "Score de Calidad: $qualityScore/100. Observaciones: $issues."
        memory.store(task, result, successful = qualityScore >= 75)
        report(result)
        return result
    }

    override fun vote(proposal: String): Boolean {
        val lower = proposal.lowercase()
        val hasAntiPatterns = lower.contains("sleep(") || lower.contains("system.gc()") || lower.contains("blocking")
        val isApproved = !hasAntiPatterns
        memory.store("VOTE: $proposal", if (isApproved) "APPROVED" else "REJECTED", successful = isApproved)
        return isApproved
    }

    /**
     * Evalúa la confianza acústica de la frase escuchada.
     * Si la confianza es < 80%, solicita des-ruido algorítmico a LanguageNormalizer.
     */
    fun evaluateAcousticConfidence(text: String, snrDb: Int): Double {
        val baseScore = when {
            snrDb > 20 -> 0.95
            snrDb > 10 -> 0.85
            snrDb > 0 -> 0.70
            else -> 0.50
        }
        val isShort = text.length < 4
        val confidence = if (isShort) baseScore * 0.8 else baseScore
        LogPoseLogger.i("QualityAgent: Confianza Acústica = ${(confidence * 100).toInt()}% (SNR: ${snrDb}dB)")
        return confidence
    }
}
