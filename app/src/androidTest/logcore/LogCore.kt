package com.uriel.logpose.logcore

import com.uriel.logpose.logcore.core.action.ActionRequest
import com.uriel.logpose.logcore.core.action.ActionResult
import com.uriel.logpose.logcore.core.intent.VoiceRequest
import com.uriel.logpose.logcore.orchestrator.ActionEngine
import com.uriel.logpose.logcore.pipeline.VoicePipeline

class LogCore(

    private val actionEngine: ActionEngine

) {

    fun execute(
        action: ActionRequest
    ): ActionResult {

        return actionEngine.dispatch(action)
    }

    fun executeVoice(
        request: VoiceRequest
    ): ActionResult {

        val action = VoicePipeline.process(request)

        return execute(action)
    }
}