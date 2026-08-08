package com.uriel.logpose.thamis.decision

import com.thamis.lab.core.contracts.command.LogPoseCommand
import com.uriel.logpose.core.Priority

/**
 * FASE 26.12 — LOGPOSE THAMIS ADVANCED DECISION ENGINE
 * FASE 6: PRIORITY RESOLVER
 */
object PriorityResolver {

    fun resolve(command: LogPoseCommand): Priority {
        return when (command) {
            is LogPoseCommand.Call -> Priority.CRITICAL
            LogPoseCommand.WhereAmI -> Priority.IMPORTANT
            is LogPoseCommand.PlayMusic, LogPoseCommand.PauseMusic -> Priority.NORMAL
            else -> Priority.LOW
        }
    }
}
