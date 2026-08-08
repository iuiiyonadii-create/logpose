package com.thamis.lab.intelligence.training

import com.thamis.lab.core.common.logging.LabLogger
import java.util.concurrent.CopyOnWriteArrayList

public data class BugFingerprint(
    public val fingerprintHash: String,
    public val bugCategory: String,
    public val summary: String,
    public val occurrenceCount: Int,
    public val severityRank: Int,
    public val recommendation: String
)

public data class KnowledgeGraphReport(
    public val totalExecutionsIndexed: Int,
    public val totalCrashesIndexed: Int,
    public val totalAnrsIndexed: Int,
    public val uniqueBugFingerprints: List<BugFingerprint>,
    public val projectHealthScore: Double
)

/**
 * LogPose Training Engine converting simulation and real-world execution telemetry into persistent engineering knowledge graphs.
 */
public class LogPoseTrainingEngine {
    private val TAG = "LogPoseTrainingEngine"
    private val executionHistory = CopyOnWriteArrayList<String>()
    private val fingerprints = CopyOnWriteArrayList<BugFingerprint>()

    public fun recordExecution(executionId: String, logs: String, hasCrash: Boolean, hasAnr: Boolean) {
        executionHistory.add(executionId)
        LabLogger.info(TAG, "Recorded execution $executionId in Training Engine.")

        if (hasCrash || hasAnr) {
            val hash = "fp-${executionId.hashCode()}"
            val fp = BugFingerprint(
                fingerprintHash = hash,
                bugCategory = if (hasAnr) "ANR" else "CRASH",
                summary = if (hasAnr) "Application Not Responding in main thread" else "Fatal NullPointer/Exception in background service",
                occurrenceCount = 1,
                severityRank = 5,
                recommendation = "Investigate main thread lock or background service exception handling."
            )
            fingerprints.add(fp)
        }
    }

    public fun generateKnowledgeGraph(): KnowledgeGraphReport {
        val total = executionHistory.size.coerceAtLeast(1)
        val crashes = fingerprints.count { it.bugCategory == "CRASH" }
        val anrs = fingerprints.count { it.bugCategory == "ANR" }
        val healthScore = if (crashes == 0 && anrs == 0) 100.0 else 85.0

        return KnowledgeGraphReport(
            totalExecutionsIndexed = total,
            totalCrashesIndexed = crashes,
            totalAnrsIndexed = anrs,
            uniqueBugFingerprints = fingerprints.toList(),
            projectHealthScore = healthScore
        )
    }

    /**
     * Records a phonetic confusion event to build the AI Confusion Matrix.
     * Supports v4.0 Semantic Drift tracking.
     */
    public fun recordConfusion(
        expected: String, 
        actual: String, 
        noiseLevel: Float,
        wasSemanticBoostUsed: Boolean = false
    ) {
        val boostTag = if (wasSemanticBoostUsed) "[SEMANTIC_BOOST]" else "[PURE_PHONETIC]"
        LabLogger.info(TAG, "Recording Confusion: '$expected' was heard as '$actual' (Noise: $noiseLevel) $boostTag")
        
        executionHistory.add("confusion-$expected-$actual-$boostTag")
    }

    public fun clear() {
        executionHistory.clear()
        fingerprints.clear()
    }
}
