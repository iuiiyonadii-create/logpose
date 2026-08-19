package com.uriel.logpose.thamis.planning.planner

import com.uriel.logpose.thamis.planning.model.*
import com.uriel.logpose.thamis.planning.strategy.PlanningStrategyEngine
import com.uriel.logpose.thamis.planning.rules.PlanningRuleEngine
import com.uriel.logpose.thamis.planning.audit.PlanningAudit
import com.uriel.logpose.thamis.planning.audit.PlanningTrace
import com.uriel.logpose.thamis.world.model.WorldSnapshot
import com.uriel.logpose.thamis.world.engine.WorldModelEngine

/**
 * Motor central de planificación cognitiva de THAMIS.
 */
object PlanningEngine {

    fun plan(goal: String, priority: PlanningPriority): ExecutionPlan {
        val world = WorldModelEngine.getCurrentSnapshot()
        val strategy = PlanningStrategyEngine.selectStrategy(world)
        
        // Generación de pasos basada en el objetivo
        val steps = generateSteps(goal)
        
        val plan = ExecutionPlan(
            goal = goal,
            priority = priority,
            risk = if (world.vehicle.riskLevel.name == "CRITICAL") 1.0f else 0.1f,
            confidence = 0.95f,
            strategy = strategy,
            estimatedDurationMs = steps.sumOf { it.estimatedTimeMs },
            conditions = listOf("BT_CONNECTED", "GPS_READY"),
            dependencies = emptyList(),
            rollback = generateRollback(goal)
        )

        val decision = PlanningRuleEngine.evaluate(plan, world)
        
        // Auditoría
        PlanningAudit.record(PlanningTrace(
            snapshotId = world.id,
            goal = goal,
            plan = plan,
            strategy = strategy,
            priority = priority,
            decision = decision,
            reason = "Planning v1.0 engine evaluation"
        ))

        return plan
    }

    private fun generateSteps(goal: String): List<PlanningStep> {
        return listOf(
            PlanningStep(description = "Validar estado", expectedResult = "SUCCESS", estimatedTimeMs = 100),
            PlanningStep(description = "Preparar hardware", expectedResult = "READY", estimatedTimeMs = 200),
            PlanningStep(description = "Ejecutar $goal", expectedResult = "COMPLETED", estimatedTimeMs = 500)
        )
    }

    private fun generateRollback(goal: String): RollbackPlan {
        return RollbackPlan(
            steps = listOf(PlanningStep(description = "Limpiar contexto de $goal", expectedResult = "CLEAN")),
            reason = "Automatic rollback for $goal"
        )
    }
}
