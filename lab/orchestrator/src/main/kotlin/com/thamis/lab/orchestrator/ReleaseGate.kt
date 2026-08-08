package com.thamis.lab.orchestrator

import com.thamis.lab.core.common.telemetry.LabTelemetry
import com.thamis.lab.intelligence.engineering.RepairStrategyResult
import com.thamis.lab.intelligence.security.SecurityAuditEngine

/**
 * ReleaseGate: Evalúa si un parche es seguro para ser integrado en el código base.
 * Implementa consenso entre el motor de calidad y el motor de seguridad.
 */
public class ReleaseGate(
    private val securityEngine: SecurityAuditEngine = SecurityAuditEngine()
) {

    public enum class ApprovalStatus {
        DEPLOY_READY,
        NEEDS_HUMAN_REVIEW,
        SECURITY_VETO,
        REJECTED
    }

    public fun evaluateRepair(repair: RepairStrategyResult, regressionScore: Double): ApprovalStatus {
        // 1. Auditoría de Seguridad proactiva sobre el parche
        // Simulamos que el motor detecta algo mal si el parche contiene palabras "inseguras"
        val isDirty = repair.strategyDescription.contains("unsafe", ignoreCase = true) || 
                      repair.strategyDescription.contains("export", ignoreCase = true)
                      
        val secReport = securityEngine.executeSecurityAudit(isSimulationDirty = isDirty)
        
        if (secReport.securityScore < 90.0) {
            LabTelemetry.logEvent("ReleaseGate", "🚨 VETO DE SEGURIDAD: ${secReport.auditSummary}")
            return ApprovalStatus.SECURITY_VETO
        }

        // 2. Evaluación de riesgo técnico
        val riskScore = calculateRisk(repair, regressionScore)
        
        LabTelemetry.logEvent("ReleaseGate", "Evaluating Repair: ${repair.failureContext} (Risk: $riskScore)")

        return when {
            riskScore > 0.95 && regressionScore == 1.0 -> ApprovalStatus.DEPLOY_READY
            riskScore > 0.70 -> ApprovalStatus.NEEDS_HUMAN_REVIEW
            else -> ApprovalStatus.REJECTED
        }
    }

    private fun calculateRisk(repair: RepairStrategyResult, regressionScore: Double): Double {
        // Lógica simplificada: Confianza de la IA * Score de Regresión
        return repair.confidenceScore * regressionScore
    }
}
