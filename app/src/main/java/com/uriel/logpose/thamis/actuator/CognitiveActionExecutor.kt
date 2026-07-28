package com.uriel.logpose.thamis.actuator

import com.uriel.logpose.thamis.cognitive.model.ThamisDecision

/**
 * Interfaz para ejecutores de acciones cognitivas.
 * Permite que THAMIS se comunique con los Managers sin acoplamiento.
 */
interface CognitiveActionExecutor {
    fun execute(decision: ThamisDecision)
}
