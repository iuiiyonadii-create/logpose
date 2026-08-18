package com.thamis.lab.evidence.certification

import com.thamis.lab.evidence.model.EvidenceStatus
import com.thamis.lab.evidence.model.ExecutionEvidence

public enum class CertificationLevel {
    READY_FOR_INTERNAL_TESTING,
    READY_FOR_CLOSED_BETA,
    READY_FOR_OPEN_BETA,
    READY_FOR_RELEASE_CANDIDATE,
    READY_FOR_PRODUCTION
}

public data class CertificationReport(
    public val level: CertificationLevel,
    public val passRate: Double,
    public val avgQuality: Double,
    public val rationale: String
)

/**
 * Evaluates execution evidence to determine official certification readiness objectively.
 * Refactored v60.0: Reasoned Quality Rationale.
 */
public class CertificationEngine {

    public fun evaluateCertification(evidences: List<ExecutionEvidence>): CertificationReport {
        if (evidences.isEmpty()) {
            return CertificationReport(
                CertificationLevel.READY_FOR_INTERNAL_TESTING, 0.0, 0.0, 
                "No evidence found to evaluate."
            )
        }

        val total = evidences.size
        val passed = evidences.count { it.status == EvidenceStatus.PASSED }
        val passRate = passed.toDouble() / total
        val avgQuality = evidences.map { it.qualityScore }.average()

        val level = when {
            passRate >= 0.99 && avgQuality >= 95.0 && total >= 100 -> CertificationLevel.READY_FOR_PRODUCTION
            passRate >= 0.95 && avgQuality >= 90.0 && total >= 50 -> CertificationLevel.READY_FOR_RELEASE_CANDIDATE
            passRate >= 0.90 && avgQuality >= 85.0 -> CertificationLevel.READY_FOR_OPEN_BETA
            passRate >= 0.80 -> CertificationLevel.READY_FOR_CLOSED_BETA
            else -> CertificationLevel.READY_FOR_INTERNAL_TESTING
        }

        val rationale = "Evaluation based on $total evidences. Pass rate: ${"%.2f".format(passRate * 100)}%. " +
                "Quality average: ${"%.2f".format(avgQuality)}. Stability achieved for ${level.name}."

        return CertificationReport(level, passRate, avgQuality, rationale)
    }
}
