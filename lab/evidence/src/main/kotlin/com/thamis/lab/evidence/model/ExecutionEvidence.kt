package com.thamis.lab.evidence.model

import java.util.UUID

public enum class EvidenceStatus {
    PASSED,
    FAILED,
    INCONCLUSIVE
}

/**
 * Immutable, traceable evidence record generated for every execution.
 */
public data class ExecutionEvidence(
    public val evidenceUuid: String = UUID.randomUUID().toString(),
    public val timestampMs: Long = System.currentTimeMillis(),
    public val gitCommit: String = "HEAD",
    public val gitBranch: String = "main",
    public val deviceId: String,
    public val androidApi: Int,
    public val scenarioId: String,
    public val executionDurationMs: Long,
    public val qualityScore: Double,
    public val cpuPercent: Double,
    public val ramMb: Double,
    public val status: EvidenceStatus = EvidenceStatus.PASSED,
    public val logTrace: String = ""
)
