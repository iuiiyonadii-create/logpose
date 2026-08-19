package com.uriel.logpose.thamis.voice.context

import com.uriel.logpose.thamis.voice.model.VoiceContext
import com.uriel.logpose.thamis.world.engine.WorldModelEngine

/**
 * Gestiona el contexto vivo de la conversación vocal.
 */
object VoiceContextManager {

    fun getCurrentContext(): VoiceContext {
        val world = WorldModelEngine.getCurrentSnapshot()
        
        return VoiceContext(
            conversationActive = world.cognitive.conversationState != "IDLE",
            systemState = world.cognitive.conversationState,
            drivingSituation = if (world.vehicle.moving) "MOVING" else "STOPPED",
            noiseLevel = 0.1f // Placeholder
        )
    }
}
