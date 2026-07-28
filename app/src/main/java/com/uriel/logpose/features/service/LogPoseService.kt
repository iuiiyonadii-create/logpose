package com.uriel.logpose.features.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.features.bluetooth.BluetoothManager
import com.uriel.logpose.features.voice.VoiceManager

/**
 * FASE 26.2 — LOGPOSE MVP CORE
 * FASE 3: LOGPOSE SERVICE
 *
 * Foreground Service que mantiene vivo el núcleo de LogPose durante la conducción.
 */
class LogPoseService : Service() {

    private lateinit var bluetoothManager: BluetoothManager
    // private lateinit var voiceManager: VoiceManager (A implementar en Fase 7)

    override fun onCreate() {
        super.onCreate()
        LogPoseLogger.i("LogPoseService: Creado")
        bluetoothManager = BluetoothManager(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogPoseLogger.i("LogPoseService: Iniciado")
        // Aquí se activaría el modo Foreground con su notificación
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        LogPoseLogger.i("LogPoseService: Destruido")
        bluetoothManager.disconnect()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
