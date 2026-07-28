package com.uriel.logpose.thamis_ai.voice

/**
 * High-level state of a voice session.
 */
enum class ConversationState {
    IDLE,
    LISTENING,
    PROCESSING,
    WAITING_CONFIRMATION,
    RESPONDING
}
