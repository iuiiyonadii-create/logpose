package com.uriel.logpose.features.navigation

import com.uriel.logpose.core.compat.core.LogPoseLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * FASE 26.4 — LOGPOSE NAVIGATION INTELLIGENCE
 * FASE 1: NAVIGATION MANAGER
 */
object NavigationManager {

    private var isNavigationActive = false

    private val _nextInstruction = MutableStateFlow<String?>(null)
    val nextInstruction = _nextInstruction.asStateFlow()

    private val _currentDestination = MutableStateFlow("Sin destino")
    val currentDestination = _currentDestination.asStateFlow()

    fun startNavigation(destination: String) {
        isNavigationActive = true
        _currentDestination.value = destination
        LogPoseLogger.i("NavigationManager: Iniciando navegación a $destination")
    }

    fun navigateTo(destination: String) {
        startNavigation(destination)
    }

    fun stopNavigation() {
        isNavigationActive = false
        LogPoseLogger.i("NavigationManager: Navegación finalizada")
    }

    fun announceLocation() {
        LogPoseLogger.i("NavigationManager: Anunciando ubicación actual")
    }

    fun updateInstruction(instruction: String) {
        _nextInstruction.value = instruction
    }

    fun getStatus(): String = if (isNavigationActive) "Navegando" else "Libre"
}
