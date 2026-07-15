package com.uriel.logpose.logcore.pipeline

import com.uriel.logpose.logcore.core.action.ActionRequest
import com.uriel.logpose.logcore.core.intent.IntentMapper
import com.uriel.logpose.logcore.core.intent.IntentParser
import com.uriel.logpose.logcore.core.intent.VoiceRequest

/**
 * Convierte una VoiceRequest en una ActionRequest lista
 * para ser ejecutada por LogCore.
 */
object VoicePipeline {

    fun process(
        request: VoiceRequest
    ): ActionRequest {

        val intent = IntentParser.parse(request)

        val capability = IntentMapper.toCapability(intent)

        return ActionRequest(
            capability = capability
        )
    }
}