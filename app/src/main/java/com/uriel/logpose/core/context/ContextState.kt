package com.uriel.logpose.core.context

/**
 * High-level context of the user's environment.
 */
enum class ContextState {
    UNKNOWN,
    IDLE,
    CONNECTED,
    DRIVING,
    STOPPED,
    PRIVATE
}
