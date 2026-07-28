package com.uriel.logpose.core.safety

/**
 * Global safety state of the rider's journey.
 */
enum class SafetyState {
    SAFE,
    ATTENTION_REQUIRED,
    RESTRICTED,
    EMERGENCY
}
