package com.uriel.logpose.core.services

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.network.PCBridge
import com.uriel.logpose.core.services.guardians.BatteryGuardian
import com.uriel.logpose.core.services.overlay.OverlayController
import com.uriel.logpose.core.telecom.LogPoseTelecom
import com.uriel.logpose.features.navigation.NavigationManager
import com.uriel.logpose.thamis.ThamisAssistant
import com.uriel.logpose.thamis.world.engine.WorldModelEngine
import com.uriel.logpose.thamis.world.model.NavigationStateV1
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripOrchestrator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pcBridge: PCBridge,
    private val batteryGuardian: BatteryGuardian,
    private val overlayController: OverlayController,
    private val communicationManager: BluetoothCommunicationManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var isTripActive = false

    fun startTrip(onSystemsReady: () -> Unit) {
        if (isTripActive) return
        isTripActive = true

        LogPoseLogger.i("TripOrchestrator: Iniciando viaje...")
        
        // Sincronización con el modelo de mundo
        WorldModelEngine.update("Orchestrator") {
            it.copy(systems = it.systems.copy(
                navigation = NavigationStateV1(isNavigating = true)
            ))
        }

        overlayController.showInvisibleOverlay()
        pcBridge.startRemoteServer()
        pcBridge.sendCommand("RIDER_ONLINE:¡Listo para el reparto!")

        ThamisAssistant.start(context)
        
        batteryGuardian.startMonitoring { pct ->
            // El servicio se encargará de actualizar la notificación con este valor
        }

        onSystemsReady()
    }

    fun endTrip() {
        if (!isTripActive) return
        isTripActive = false

        LogPoseLogger.i("TripOrchestrator: Finalizando viaje...")
        
        WorldModelEngine.update("Orchestrator") {
            it.copy(systems = it.systems.copy(
                navigation = NavigationStateV1(isNavigating = false)
            ))
        }

        NavigationManager.stopNavigation()
        ThamisAssistant.stop()
        pcBridge.stopRemoteServer()
        batteryGuardian.stopMonitoring()
        overlayController.hideOverlay()
        communicationManager.stopCommunication()
    }
}
