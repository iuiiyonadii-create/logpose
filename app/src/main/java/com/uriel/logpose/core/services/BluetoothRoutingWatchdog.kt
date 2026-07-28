package com.uriel.logpose.core.services

import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * BluetoothRoutingWatchdog: Vigilante del ruteo de audio.
 * Si el casco no engancha en el tiempo estipulado, habilita el micro del celu.
 */
class BluetoothRoutingWatchdog(
    private val audioManager: AudioManager,
    private val tag: String = "Watchdog",
    private val onRouted: (AudioDeviceInfo) -> Unit,
    private val onFallback: () -> Unit
) {
    private val handler = Handler(Looper.getMainLooper())
    private var resolved = false

    private val deviceCallback = object : AudioDeviceCallback() {
        override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>) {
            val scoDevice = addedDevices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO }
            scoDevice?.let {
                if (!resolved) {
                    resolved = true
                    handler.removeCallbacksAndMessages(null)
                    LogPoseLogger.i("$tag: ¡Casco detectado justo a tiempo!")
                    onRouted(it)
                } else {
                    LogPoseLogger.i("$tag: Casco conectado tarde. Cambiando ruteo ahora.")
                    onRouted(it)
                }
            }
        }
    }

    fun start(graceMs: Long = 3000L) {
        resolved = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            audioManager.registerAudioDeviceCallback(deviceCallback, handler)
            
            // Chequeo inmediato
            val already = audioManager.availableCommunicationDevices
                .firstOrNull { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO }
            
            if (already != null) {
                resolved = true
                LogPoseLogger.i("$tag: Casco ya estaba listo.")
                onRouted(already)
                return
            }
        }

        handler.postDelayed({
            if (!resolved) {
                LogPoseLogger.w("$tag: El casco no respondió en ${graceMs}ms. Usando altavoz.")
                onFallback()
            }
        }, graceMs)
    }

    fun stop() {
        handler.removeCallbacksAndMessages(null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            audioManager.unregisterAudioDeviceCallback(deviceCallback)
        }
    }
}
