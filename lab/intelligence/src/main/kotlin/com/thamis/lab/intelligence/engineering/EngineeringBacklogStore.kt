package com.thamis.lab.intelligence.engineering

import com.thamis.lab.core.common.logging.LabLogger
import java.util.concurrent.CopyOnWriteArrayList

public data class BacklogItem(
    public val taskId: String,
    public val title: String,
    public val description: String,
    public val priority: String, // CRITICAL, HIGH, MEDIUM, LOW
    public val risk: String,
    public val complexity: String,
    public val estimatedHours: Double,
    public val affectedModules: List<String>,
    public val status: String // PENDING, IN_PROGRESS, COMPLETED, VALIDATED
)

/**
 * Engineering Backlog Store managing active engineering tasks, priorities, risks, and module impacts.
 */
public class EngineeringBacklogStore {
    private val TAG = "EngineeringBacklogStore"
    private val backlog = CopyOnWriteArrayList<BacklogItem>()

    public fun addTask(item: BacklogItem) {
        backlog.add(item)
        LabLogger.info(TAG, "Added task '${item.taskId}' (${item.title}) to Backlog Store.")
    }

    public fun getPrioritizedTasks(): List<BacklogItem> {
        val priorityMap = mapOf("CRITICAL" to 4, "HIGH" to 3, "MEDIUM" to 2, "LOW" to 1)
        return backlog.sortedByDescending { priorityMap[it.priority] ?: 0 }
    }
}
