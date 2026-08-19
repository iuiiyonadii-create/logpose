package com.uriel.logpose.thamis.orchestration.engine

import com.uriel.logpose.thamis.orchestration.model.*
import com.uriel.logpose.thamis.orchestration.queue.PriorityQueueManager
import com.uriel.logpose.thamis.orchestration.scheduler.ActionScheduler
import com.uriel.logpose.thamis.orchestration.trace.OrchestrationAudit
import com.uriel.logpose.thamis.orchestration.trace.OrchestrationTrace
import com.uriel.logpose.thamis.world.context.WorldContextEngine

/**
 * GlobalOrchestrationEngine v1.0: El director de orquesta de LogPose.
 */
object GlobalOrchestrationEngine {

    private var activeActions = mutableListOf<RunningAction>()
    private var blockedActions = mutableListOf<BlockedAction>()

    /**
     * Punto de entrada para nuevas acciones sugeridas por el DecisionEngine.
     */
    fun orchestrate(pendingAction: PendingAction): OrchestrationDecision {
        val worldSnapshot = WorldContextEngine.getCurrentState()
        
        val decision = ActionScheduler.schedule(pendingAction, activeActions, worldSnapshot)
        
        when (decision) {
            OrchestrationDecision.EXECUTE_NOW -> {
                startAction(pendingAction)
            }
            OrchestrationDecision.WAIT -> {
                PriorityQueueManager.enqueue(pendingAction)
            }
            else -> {
                // Ignore or cancel
            }
        }

        recordTrace(pendingAction, decision, "Decisión del motor global")
        return decision
    }

    private fun startAction(action: PendingAction) {
        activeActions.add(RunningAction(action))
    }

    fun finishAction(actionId: String) {
        activeActions.removeIf { it.pendingAction.id == actionId }
        processQueue()
    }

    private fun processQueue() {
        val next = PriorityQueueManager.poll() ?: return
        orchestrate(next)
    }

    private fun recordTrace(action: PendingAction, decision: OrchestrationDecision, reason: String) {
        OrchestrationAudit.record(OrchestrationTrace(
            actionId = action.id,
            domain = action.domain,
            decision = decision,
            priority = action.priority,
            reason = reason
        ))
    }

    fun getState(): OrchestrationState {
        return OrchestrationState(
            activeActions = activeActions.toList(),
            queuedActions = PriorityQueueManager.getQueuedActions(),
            blockedActions = blockedActions.toList()
        )
    }
}
