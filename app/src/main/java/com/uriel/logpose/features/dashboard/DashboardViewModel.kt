package com.uriel.logpose.features.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.services.LogPoseCallService
import com.uriel.logpose.core.telecom.LogPoseTelecom
import com.uriel.logpose.core.utils.BatteryMonitor
import com.uriel.logpose.features.navigation.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

enum class TripStatus { IDLE, CONNECTING, ACTIVE, ERROR }

/**
 * TripUiState v5.0: Estado unificado para el Dashboard del casco.
 * Incluye navegación, batería dual y visor de comandos.
 */
data class TripUiState(
    val isTripActive: Boolean = false,
    val tripStatus: TripStatus = TripStatus.IDLE,
    val phoneBatteryPct: Int = 100,
    val headsetConnected: Boolean = false,
    val headsetBatteryPct: Int? = null,
    val bannerText: String? = null,
    val buttonLabel: String = "INICIAR",
    val lastRecognizedCommand: String? = null,
    val lastCommandConfidence: Float? = null,
    val navigationInstruction: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val telecom: LogPoseTelecom
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripUiState())
    val uiState = _uiState.asStateFlow()

    private var callService: LogPoseCallService? = null
    private var collectJob: Job? = null
    private val batteryMonitor = BatteryMonitor(appContext)

    init {
        // 1. Polling de batería para la UI (Cada 1 minuto)
        viewModelScope.launch {
            while (isActive) {
                _uiState.update { 
                    it.copy(
                        phoneBatteryPct = batteryMonitor.phoneBatteryPct,
                        headsetBatteryPct = batteryMonitor.tryGetHeadsetBattery()
                    )
                }
                delay(60_000)
            }
        }

        // 2. Observamos instrucciones de navegación globales
        NavigationManager.nextInstruction
            .onEach { instruction ->
                _uiState.update { it.copy(navigationInstruction = instruction) }
            }
            .launchIn(viewModelScope)
    }

    fun attachToService(service: LogPoseCallService) {
        this.callService = service
        
        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            combine(
                service.tripStatus,
                service.bannerText
            ) { status, banner ->
                val isActive = status == LogPoseCallService.ServiceTripStatus.ACTIVE
                val isConnecting = status == LogPoseCallService.ServiceTripStatus.CONNECTING
                
                val label = when {
                    isConnecting -> "CONECTANDO..."
                    isActive -> "FINALIZAR VIAJE"
                    else -> "INICIAR VIAJE"
                }

                _uiState.value.copy(
                    isTripActive = isActive,
                    tripStatus = mapStatus(status),
                    bannerText = banner,
                    buttonLabel = label,
                    headsetConnected = service.isHeadsetConnected
                )
            }.collect { newState ->
                _uiState.update { newState }
            }
        }

        // 3. Puente de comandos para el visor de la pantalla
        service.recognizedCommands
            .onEach { result ->
                _uiState.update {
                    it.copy(
                        lastRecognizedCommand = result.text,
                        lastCommandConfidence = result.confidence
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun mapStatus(status: LogPoseCallService.ServiceTripStatus) = when(status) {
        LogPoseCallService.ServiceTripStatus.IDLE -> TripStatus.IDLE
        LogPoseCallService.ServiceTripStatus.CONNECTING -> TripStatus.CONNECTING
        LogPoseCallService.ServiceTripStatus.ACTIVE -> TripStatus.ACTIVE
        LogPoseCallService.ServiceTripStatus.ERROR -> TripStatus.ERROR
    }

    fun startTrip() { callService?.startTrip() }
    fun endTrip() { callService?.endTrip() }
    fun dismissBanner() { callService?.dismissBanner() }

    override fun onCleared() {
        super.onCleared()
        collectJob?.cancel()
    }
}

