package com.uriel.logpose.features.safety

import com.uriel.logpose.core.Command
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * FASE 26.7 — LOGPOSE DRIVING SAFETY INTELLIGENCE
 * FASE 1: SAFETY ENGINE CORE
 */
object SafetyEngine {

    /**
     * Valida si un comando es seguro dadas las condiciones actuales.
     */
    fun isActionAllowed(command: Command, isMoving: Boolean): Boolean {
        if (isMoving && command == Command.CALL_CONTACT) {
            LogPoseLogger.w("SafetyEngine: Bloqueando llamada directa durante movimiento rápido.")
            return false
        }
        return true
    }
}
