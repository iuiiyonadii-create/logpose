package com.uriel.logpose.thamis.decision

import com.uriel.logpose.core.Command
import com.uriel.logpose.core.Priority

/**
 * FASE 26.12 — LOGPOSE THAMIS ADVANCED DECISION ENGINE
 * FASE 6: PRIORITY RESOLVER
 */
object PriorityResolver {

    fun resolve(command: Command): Priority {
        return when (command) {
            Command.CALL_CONTACT -> Priority.CRITICAL
            Command.GET_LOCATION -> Priority.IMPORTANT
            Command.PLAY_MUSIC, Command.PAUSE_MUSIC -> Priority.NORMAL
            else -> Priority.LOW
        }
    }
}
