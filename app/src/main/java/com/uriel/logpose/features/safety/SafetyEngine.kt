package com.uriel.logpose.features.safety

import com.thamis.lab.core.contracts.command.LogPoseCommand
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * FASE 26.7 — LOGPOSE DRIVING SAFETY INTELLIGENCE
 * FASE 1: SAFETY ENGINE CORE
 */
object SafetyEngine {

    /**
     * Valida si un comando es seguro dadas las condiciones actuales.
     */
    fun isActionAllowed(command: LogPoseCommand, isMoving: Boolean): Boolean {
        if (isMoving && command is LogPoseCommand.Call) {
            LogPoseLogger.w("SafetyEngine: Bloqueando llamada directa durante movimiento rápido.")
            return false
        }
        return true
    }
}
