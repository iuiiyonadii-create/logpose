package com.uriel.logpose.core.engine

import com.uriel.logpose.core.compat.core.AppState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * LogPoseEngine: Bridge object for legacy engine references.
 * Proxies states to ThamisCore.
 */
object LogPoseEngine {
    private val _state = MutableStateFlow(AppState.READY)
    val state = _state.asStateFlow()

    fun setWaitingSendConfirmation() {
        // Implementation handled by ThamisCore in modern flow
    }
}
