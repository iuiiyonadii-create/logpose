package com.thamis.lab.intelligence.engineering

import com.thamis.lab.core.common.logging.LabLogger
import java.util.concurrent.CopyOnWriteArrayList

public data class BugRecord(
    public val bugId: String,
    public val fingerprintHash: String,
    public val category: String,
    public val severity: String, // LOW, MEDIUM, HIGH, CRITICAL
    public val frequencyCount: Int,
    public val estimatedEngineeringCost: String,
    public val userImpactScore: Double
)

/**
 * Bug Intelligence Engine clustering crashes, creating fingerprints, and ranking engineering backlogs.
 */
public class BugIntelligenceEngine {
    private val TAG = "BugIntelligenceEngine"
    private val bugStore = CopyOnWriteArrayList<BugRecord>()

    public fun registerBug(bug: BugRecord) {
        bugStore.add(bug)
        LabLogger.info(TAG, "Registered bug ${bug.bugId} in Bug Intelligence Engine.")
    }

    public fun getPrioritizedBacklog(): List<BugRecord> {
        return bugStore.sortedByDescending { it.userImpactScore }
    }
}
