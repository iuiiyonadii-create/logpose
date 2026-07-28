package com.uriel.logpose.thamis.optimization.scheduler

import com.uriel.logpose.thamis.optimization.model.ResourcePriority
import java.util.concurrent.PriorityBlockingQueue

/**
 * Organiza procesos internos para optimizar ciclos de procesamiento.
 */
object ResourceScheduler {
    private val taskQueue = PriorityBlockingQueue<ScheduledTask>(10, compareBy { it.priority })

    data class ScheduledTask(val id: String, val priority: ResourcePriority, val action: () -> Unit)

    fun schedule(id: String, priority: ResourcePriority, action: () -> Unit) {
        taskQueue.add(ScheduledTask(id, priority, action))
    }

    fun tick() {
        val task = taskQueue.poll() ?: return
        task.action()
    }

    fun clear() = taskQueue.clear()
}
