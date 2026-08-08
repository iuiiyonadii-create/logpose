package com.uriel.logpose.core.services

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Monitorea el estado REAL del perfil HFP (Manos libres).
 * Ayuda a detectar si el casco está en modo 'Solo música' o si se perdió el canal.
 */
object BluetoothHfpMonitor {
    private var headsetProxy: BluetoothHeadset? = null

    fun initialize(context: Context) {
        val adapter = BluetoothAdapter.getDefaultAdapter() ?: return
        adapter.getProfileProxy(context, object : BluetoothProfile.ServiceListener {
            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                if (profile == BluetoothProfile.HEADSET) {
                    headsetProxy = proxy as BluetoothHeadset
                    LogPoseLogger.d("BT: Monitor HFP listo.")
                }
            }
            override fun onServiceDisconnected(profile: Int) {
                headsetProxy = null
            }
        }, BluetoothProfile.HEADSET)
    }

    /**
     * Verifica si el canal HFP está realmente negociado.
     * Hardened: Algunos cascos chinos (V6) mantienen el A2DP pero cierran el HFP para ahorrar batería.
     */
    fun isHfpConnected(): Boolean {
        try {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    LogPoseApplication.instance,
                    android.Manifest.permission.BLUETOOTH_CONNECT
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }

            val devices = headsetProxy?.connectedDevices
            val connected = !devices.isNullOrEmpty()
            
            if (connected) {
                val device = devices?.first()
                LogPoseLogger.i("BT: Casco HFP=${device?.name} detectado y listo.")
            }
            return connected
        } catch (e: Exception) {
            LogPoseLogger.w("BT: Fallo al consultar estado HFP: ${e.message}")
            return false
        }
    }
}
