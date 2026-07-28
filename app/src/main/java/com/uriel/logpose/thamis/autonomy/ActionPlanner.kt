package com.uriel.logpose.thamis.autonomy

/**
 * FASE 25.19 — THAMIS AUTONOMOUS ASSISTANCE FRAMEWORK
 * FASE 2: ACTION PLANNER
 */
object ActionPlanner {

    sealed class PlannedAction {
        object ResumeMusic : PlannedAction()
        object StartFocusMode : PlannedAction()
        data class SuggestRoute(val destination: String) : PlannedAction()
    }

    /**
     * Propone una acción basada en el contexto.
     */
    fun planActions(bluetoothConnected: Boolean, isRiding: Boolean): List<PlannedAction> {
        val plans = mutableListOf<PlannedAction>()
        if (bluetoothConnected && !isRiding) {
            plans.add(PlannedAction.ResumeMusic)
        }
        return plans
    }
}
