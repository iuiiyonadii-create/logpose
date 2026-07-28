package com.uriel.logpose.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.uriel.logpose.core.bluetooth.BluetoothManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * LogPose Foreground Service
 * Keeps the application alive and coordinating Bluetooth/Voice in the background.
 */
@AndroidEntryPoint
class LogPoseService : Service() {

    @Inject
    lateinit var bluetoothManager: BluetoothManager

    private lateinit var notificationManager: ServiceNotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = ServiceNotificationManager(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startLogPose()
            ACTION_STOP -> stopLogPose()
        }
        return START_STICKY
    }

    private fun startLogPose() {
        val notification = notificationManager.createForegroundNotification("Copilot Active")
        startForeground(NOTIFICATION_ID, notification)
        
        // Start bluetooth observation
        bluetoothManager.startDiscovery()
    }

    private fun stopLogPose() {
        bluetoothManager.cancelDiscovery()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val NOTIFICATION_ID = 101
        const val ACTION_START = "com.uriel.logpose.ACTION_START"
        const val ACTION_STOP = "com.uriel.logpose.ACTION_STOP"
    }
}
