package com.thamis.lab.intelligence.roadmap

import com.thamis.lab.core.common.logging.LabLogger
import java.util.concurrent.CopyOnWriteArrayList

public data class RoadmapMilestone(
    public val milestoneId: String,
    public val title: String,
    public val priorityRank: Int,
    public val estimatedComplexity: String,
    public val engineeringValue: String,
    public val isCompleted: Boolean
)

/**
 * Roadmap Engine maintaining live engineering milestones, risk levels, and automated task prioritization.
 */
public class RoadmapEngine {
    private val TAG = "RoadmapEngine"
    private val milestones = CopyOnWriteArrayList<RoadmapMilestone>()

    public fun addMilestone(milestone: RoadmapMilestone) {
        milestones.add(milestone)
        LabLogger.info(TAG, "Added milestone '${milestone.milestoneId}' (${milestone.title}) to Roadmap Engine.")
    }

    public fun getActiveRoadmap(): List<RoadmapMilestone> {
        return milestones.sortedBy { it.priorityRank }
    }
}
