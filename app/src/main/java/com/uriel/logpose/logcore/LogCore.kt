package com.uriel.logpose.logcore

import com.uriel.logpose.logcore.core.action.ActionRequest
import com.uriel.logpose.logcore.core.action.ActionResult
import com.uriel.logpose.logcore.orchestrator.ActionEngine

class LogCore(

    private val actionEngine: ActionEngine

) {

    fun execute(
        action: ActionRequest
    ): ActionResult {

        return actionEngine.dispatch(action)

    }

}