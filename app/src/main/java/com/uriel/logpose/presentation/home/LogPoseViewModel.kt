package com.uriel.logpose.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uriel.logpose.domain.models.BluetoothStatus
import com.uriel.logpose.domain.notifications.NotificationEvent
import com.uriel.logpose.domain.repositories.BluetoothRepository
import com.uriel.logpose.core.thamis.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class LogPoseViewModel @Inject constructor(
    private val bluetoothRepository: BluetoothRepository,
    private val eventBus: EventBus
) : ViewModel() {

    private val _isServiceRunning = MutableStateFlow(false)
    private val _lastNotification = MutableStateFlow<NotificationEvent?>(null)

    val uiState: StateFlow<LogPoseUiState> = combine(
        bluetoothRepository.connectionState,
        _isServiceRunning,
        _lastNotification
    ) { btStatus, serviceRunning, lastNotif ->
        LogPoseUiState(
            bluetoothStatus = btStatus,
            isServiceRunning = serviceRunning,
            lastNotification = lastNotif
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LogPoseUiState()
    )

    init {
        // Observe system events via EventBus
        eventBus.events.onEach { event ->
            if (event.data is NotificationEvent) {
                _lastNotification.value = event.data
            }
        }.launchIn(viewModelScope)
    }

    fun toggleService() {
        _isServiceRunning.value = !_isServiceRunning.value
    }
}

data class LogPoseUiState(
    val bluetoothStatus: BluetoothStatus? = null,
    val isServiceRunning: Boolean = false,
    val lastNotification: NotificationEvent? = null
)
