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

/**
 * Evaluates execution evidence to determine official certification readiness objectively.
 */
public class CertificationEngine {

    public fun evaluateCertification(evidences: List<ExecutionEvidence>): CertificationLevel {
        if (evidences.isEmpty()) return CertificationLevel.READY_FOR_INTERNAL_TESTING

        val total = evidences.size
        val passed = evidences.count { it.status == EvidenceStatus.PASSED }
        val passRate = passed.toDouble() / total
        val avgQuality = evidences.map { it.qualityScore }.average()

        return when {
            passRate >= 0.99 && avgQuality >= 95.0 && total >= 100 -> CertificationLevel.READY_FOR_PRODUCTION
            passRate >= 0.95 && avgQuality >= 90.0 && total >= 50 -> CertificationLevel.READY_FOR_RELEASE_CANDIDATE
            passRate >= 0.90 && avgQuality >= 85.0 -> CertificationLevel.READY_FOR_OPEN_BETA
            passRate >= 0.80 -> CertificationLevel.READY_FOR_CLOSED_BETA
            else -> CertificationLevel.READY_FOR_INTERNAL_TESTING
        }
    }
}
