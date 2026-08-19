package com.uriel.logpose.thamis.validation.audio

/**
 * Registra el ciclo de vida del ruteo de audio durante una interacción con THAMIS.
 */
data class AudioRoutingTrace(
    val timestamp: Long = System.currentTimeMillis(),
    val bluetoothState: String,
    val stateBefore: ScoState,
    val stateDuring: ScoState,
    val stateAfter: ScoState,
    val durationMs: Long,
    val result: String
) {
    enum class ScoState {
        SCO_DISCONNECTED,
        SCO_OPENING,
        SCO_CONNECTED,
        LISTENING,
        REASONING,
        EXECUTING,
        SCO_RELEASING,
        SCO_RELEASED,
        ERROR
    }
}
