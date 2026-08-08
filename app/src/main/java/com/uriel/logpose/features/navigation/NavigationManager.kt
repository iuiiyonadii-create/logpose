package com.uriel.logpose.features.navigation

import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.compat.core.LogPoseLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        if (destination.isBlank()) {
            LogPoseLogger.w("NavigationManager: Solicitud de navegación sin destino. Abriendo mapas...")
        } else {
            LogPoseLogger.i("NavigationManager: Iniciando navegación a $destination")
        }

        isNavigationActive = true
        _currentDestination.value = if (destination.isBlank()) "Explorar" else destination

        val provider = com.uriel.logpose.thamis.navigation.provider.NavigationProviderFactory.getBestAvailableProvider()
        val result = if (destination.isBlank()) provider.open() else provider.navigate(destination)

        if (result.success) {
            LogPoseLogger.i("NavigationManager: Acción ejecutada con éxito vía ${result.provider}")
            // FASE 3: Reportar a través del EventBus en el scope principal
            CoroutineScope(Dispatchers.Main).launch {
                LogPoseApplication.entryPoint.eventBus().emit(
                    com.uriel.logpose.core.thamis.Event(
                        id = "nav_${System.currentTimeMillis()}",
                        type = com.uriel.logpose.core.thamis.EventType.NAVIGATION_UPDATE,
                        source = "NavigationManager",
                        data = mapOf("dest" to destination, "provider" to result.provider.name)
                    )
                )
            }
        } else {
            LogPoseLogger.e("NavigationManager: Falló acción de navegación: ${result.reason}")
            isNavigationActive = false
        }
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
