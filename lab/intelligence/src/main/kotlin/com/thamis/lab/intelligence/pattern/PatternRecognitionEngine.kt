package com.thamis.lab.intelligence.pattern

public data class ErrorCluster(
    public val clusterId: String,
    public val errorPattern: String,
    public val occurrenceCount: Int
)

/**
 * Pattern Recognition Engine for clustering similar errors and detecting usage patterns.
 */
public class PatternRecognitionEngine {

    public fun clusterFailures(failureReasons: List<String>): List<ErrorCluster> {
        val grouped = failureReasons.groupBy { extractPattern(it) }
        return grouped.entries.mapIndexed { index, entry ->
            ErrorCluster(
                clusterId = "cluster-${index + 1}",
                errorPattern = entry.key,
                occurrenceCount = entry.value.size
            )
        }
    }

    private fun extractPattern(reason: String): String {
        return when {
            reason.contains("Expected intent") -> "INTENT_MISMATCH"
            reason.contains("BLUETOOTH") -> "BLUETOOTH_FAULT"
            reason.contains("GPS") -> "GPS_FAULT"
            else -> "GENERIC_FAILURE"
        }
    }
}
