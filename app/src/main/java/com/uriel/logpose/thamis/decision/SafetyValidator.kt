package com.uriel.logpose.thamis.decision

import com.thamis.lab.core.contracts.command.LogPoseCommand
import com.uriel.logpose.thamis.context.ActivityDetector

/**
 * FASE 26.12 — LOGPOSE THAMIS ADVANCED DECISION ENGINE
 * FASE 9: SAFETY VALIDATOR
 */
object SafetyValidator {

    fun isSafe(command: LogPoseCommand): Boolean {
        val isRiding = ActivityDetector.getActivity() == ActivityDetector.Activity.RIDING
        
        return when (command) {
            is LogPoseCommand.Call -> !isRiding
            LogPoseCommand.StopListening -> true
            else -> true
        }
    }
}
