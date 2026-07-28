package com.uriel.logpose.thamis.recovery.engine

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.monitoring.model.DiagnosticReport
import com.uriel.logpose.thamis.recovery.action.RecoveryPlanner
import com.uriel.logpose.thamis.recovery.history.RecoveryHistory
import com.uriel.logpose.thamis.recovery.model.*
import com.uriel.logpose.thamis.recovery.policy.RecoveryPolicyEngine
import com.uriel.logpose.thamis.recovery.strategy.RecoveryStrategyEngine
import com.uriel.logpose.thamis.recovery.trace.RecoveryAudit
import com.uriel.logpose.thamis.recovery.trace.RecoveryTrace
import com.uriel.logpose.thamis.world.engine.WorldModelEngine

/**
 * Motor central de recuperación automática de THAMIS v1.0.
 */
object AutoRecoveryEngine {

    fun analyzeAnomaly(anomaly: com.uriel.logpose.thamis.monitoring.model.Anomaly, report: DiagnosticReport): RecoveryPlan? {
        val world = WorldModelEngine.getCurrentSnapshot()
        
        // 1. Seleccionar Estrategia
        val strategy = RecoveryStrategyEngine.select(anomaly.type)
        
        // 2. Generar Plan
        val plan = RecoveryPlanner.createPlan(anomaly, report, strategy)
        
        // 3. Evaluar Política
        val decision = RecoveryPolicyEngine.evaluate(plan, world)
        
        // 4. Auditoría
        RecoveryAudit.record(RecoveryTrace(
            snapshotId = world.id,
            anomalyType = anomaly.type.name,
            planId = plan.id,
            strategy = strategy.name,
            decision = decision
        ))

        LogPoseLogger.d("[THAMIS_RECOVERY] Anomalía: ${anomaly.type} | Estrategia: $strategy | Decisión: $decision")

        return if (decision == RecoveryDecision.APPROVED) {
            RecoveryHistory.record(plan, decision)
            plan
        } else {
            null
        }
    }
}
