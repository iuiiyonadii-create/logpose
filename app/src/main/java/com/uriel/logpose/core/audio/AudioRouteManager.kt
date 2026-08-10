package com.uriel.logpose.core.audio

import android.content.Context
import android.media.AudioManager
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Manages where the audio is being routed (Bluetooth vs Speaker).
 */
class AudioRouteManager(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun startBluetoothSco() {
        if (!audioManager.isBluetoothScoAvailableOffCall) {
            LogPoseLogger.e("AudioRoute", "Bluetooth SCO not available")
            return
        }
        audioManager.startBluetoothSco()
        audioManager.isBluetoothScoOn = true
        LogPoseLogger.d("AudioRoute", "Bluetooth SCO started")
    }

    fun stopBluetoothSco() {
        audioManager.stopBluetoothSco()
        audioManager.isBluetoothScoOn = false
        LogPoseLogger.d("AudioRoute", "Bluetooth SCO stopped")
    }

    fun isBluetoothAudioRouteActive(): Boolean {
        return audioManager.isBluetoothA2dpOn || audioManager.isBluetoothScoOn
    }
}
