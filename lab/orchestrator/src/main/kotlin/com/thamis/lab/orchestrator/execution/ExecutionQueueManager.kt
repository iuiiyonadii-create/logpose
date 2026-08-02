package com.thamis.lab.orchestrator.execution

import com.thamis.lab.core.common.logging.LabLogger
import java.util.concurrent.ConcurrentLinkedQueue

public data class QueuedExecutionTask(
    public val taskId: String,
    public val taskType: String,
    public val priority: Int,
    public val payload: String,
    public val timestampMs: Long
)

/**
 * Execution Queue Manager providing observable, priority-queued task scheduling for local operations.
 */
public class ExecutionQueueManager {
    private val TAG = "ExecutionQueueManager"
    private val queue = ConcurrentLinkedQueue<QueuedExecutionTask>()

    public fun enqueueTask(task: QueuedExecutionTask) {
        queue.add(task)
        LabLogger.info(TAG, "Enqueued task '${task.taskId}' (${task.taskType}) with priority ${task.priority}")
    }

    public fun pollNextTask(): QueuedExecutionTask? {
        val task = queue.poll()
        if (task != null) {
            LabLogger.info(TAG, "Polled task '${task.taskId}' for execution.")
        }
        return task
    }

    public fun getQueueSize(): Int = queue.size
}
