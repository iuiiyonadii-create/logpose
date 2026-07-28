package com.uriel.logpose.thamis_ai.final

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * The ultimate source of truth for the complete system state.
 */
class FinalStateManager {
    private val _systemStatus = MutableStateFlow("READY")
    val systemStatus = _systemStatus.asStateFlow()

    fun updateStatus(status: String) {
        _systemStatus.value = status
    }
}
