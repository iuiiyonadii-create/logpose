package com.uriel.logpose.thamis.decision

import com.uriel.logpose.core.Command
import com.uriel.logpose.thamis.context.ActivityDetector

/**
 * FASE 26.12 — LOGPOSE THAMIS ADVANCED DECISION ENGINE
 * FASE 9: SAFETY VALIDATOR
 */
object SafetyValidator {

    fun isSafe(command: Command): Boolean {
        val isRiding = ActivityDetector.getActivity() == ActivityDetector.Activity.RIDING
        
        return when (command) {
            Command.CALL_CONTACT -> !isRiding
            Command.STOP_LOGPOSE -> true
            else -> true
        }
    }
}
