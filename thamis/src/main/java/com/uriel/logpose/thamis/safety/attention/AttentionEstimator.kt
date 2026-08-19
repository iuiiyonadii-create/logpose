package com.uriel.logpose.thamis.safety.attention

import com.uriel.logpose.thamis.safety.model.AttentionState
import com.uriel.logpose.thamis.safety.model.RidingContext

/**
 * Estima la carga de interacción y la disponibilidad de atención del usuario.
 */
object AttentionEstimator {

    fun estimate(context: RidingContext): AttentionState {
        val load = context.estimatedCognitiveLoad
        
        return when {
            load > 0.8f -> AttentionState.HIGH_LOAD
            load > 0.4f || context.isCallActive -> AttentionState.BUSY
            else -> AttentionState.AVAILABLE
        }
    }
}
