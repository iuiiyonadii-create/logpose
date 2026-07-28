package com.uriel.logpose.core.safety

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Core engine for evaluating if a system action is safe to perform.
 */
class SafetyEngine {

    private val _state = MutableStateFlow(SafetyState.SAFE)
    val state = _state.asStateFlow()

    fun evaluate(priority: PriorityLevel): Boolean {
        Log.d("SafetyEngine", "Evaluating priority: $priority in current state: ${_state.value}")
        
        return when (_state.value) {
            SafetyState.SAFE -> true
            SafetyState.ATTENTION_REQUIRED -> priority != PriorityLevel.INFORMATIONAL
            SafetyState.RESTRICTED -> priority == PriorityLevel.CRITICAL
            SafetyState.EMERGENCY -> false
        }
    }

    fun updateState(newState: SafetyState) {
        _state.value = newState
    }
}
