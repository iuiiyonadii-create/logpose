package com.uriel.logpose.logcore

import com.uriel.logpose.logcore.core.action.ActionRequest
import com.uriel.logpose.logcore.orchestrator.ActionEngine

/**
 * API pública de LogCore.
 *
 * Toda la aplicación debe comunicarse únicamente con esta clase.
 */
class LogCore(

    private val actionEngine: ActionEngine

) {

    fun execute(action: ActionRequest) {
        actionEngine.dispatch(action)
    }

}