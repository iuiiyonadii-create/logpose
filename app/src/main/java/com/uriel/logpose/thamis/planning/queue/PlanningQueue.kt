package com.uriel.logpose.thamis.planning.queue

import com.uriel.logpose.thamis.planning.model.ExecutionPlan
import java.util.concurrent.PriorityBlockingQueue

/**
 * Cola de planes ordenada por prioridad cognitiva.
 */
object PlanningQueue {
    private val queue = PriorityBlockingQueue<ExecutionPlan>(11, compareByDescending { it.priority })

    fun enqueue(plan: ExecutionPlan) {
        // De-duplicación por objetivo
        if (queue.any { it.goal == plan.goal }) return
        queue.add(plan)
    }

    fun poll(): ExecutionPlan? = queue.poll()
    fun peek(): ExecutionPlan? = queue.peek()
    fun clear() = queue.clear()
    fun size(): Int = queue.size
}
