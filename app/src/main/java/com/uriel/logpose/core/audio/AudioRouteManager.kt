package com.uriel.logpose.core.audio

import android.content.Context
import android.media.AudioManager
import android.util.Log

/**
 * Manages where the audio is being routed (Bluetooth vs Speaker).
 */
class AudioRouteManager(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun startBluetoothSco() {
        if (!audioManager.isBluetoothScoAvailableOffCall) {
            Log.e("AudioRoute", "Bluetooth SCO not available")
            return
        }
        audioManager.startBluetoothSco()
        audioManager.isBluetoothScoOn = true
        Log.d("AudioRoute", "Bluetooth SCO started")
    }

    fun stopBluetoothSco() {
        audioManager.stopBluetoothSco()
        audioManager.isBluetoothScoOn = false
        Log.d("AudioRoute", "Bluetooth SCO stopped")
    }

    fun isBluetoothAudioRouteActive(): Boolean {
        return audioManager.isBluetoothA2dpOn || audioManager.isBluetoothScoOn
    }
}
