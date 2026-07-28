package com.uriel.logpose.core.services

import android.content.Context
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import com.uriel.logpose.core.compat.core.LogPoseLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScoConnectionMonitor(
    private val context: Context,
    private val sessionManager: TripSessionManager
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var recoveryTimeoutJob: Job? = null

    private val callback = object : AudioDeviceCallback() {
        override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>) {
            if (removedDevices.any { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO }) {
                sessionManager.onHeadsetDropped()
                scheduleRecoveryWindow()
            }
        }

        override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>) {
            val scoDevice = addedDevices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO }
            if (scoDevice != null) {
                LogPoseLogger.i("Hardware: Casco reapareció. Re-anclando audio.")
                recoveryTimeoutJob?.cancel()
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    audioManager.setCommunicationDevice(scoDevice)
                }
                sessionManager.onHeadsetRecovered()
            }
        }
    }

    fun start() {
        audioManager.registerAudioDeviceCallback(callback, Handler(Looper.getMainLooper()))
    }

    fun stop() {
        audioManager.unregisterAudioDeviceCallback(callback)
    }

    private fun scheduleRecoveryWindow() {
        // Ventana de 8 segundos para recuperar el casco sin matar la conexión
        recoveryTimeoutJob?.cancel()
        recoveryTimeoutJob = CoroutineScope(Dispatchers.Default).launch {
            delay(8000)
            if (sessionManager.state.value == TripSessionState.RECONNECTING) {
                LogPoseLogger.e("Hardware: El casco no volvió. Cerrando sesión de viaje.")
                sessionManager.endTrip()
            }
        }
    }
}
