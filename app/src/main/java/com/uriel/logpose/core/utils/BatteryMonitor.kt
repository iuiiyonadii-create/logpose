package com.uriel.logpose.core.utils

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.BatteryManager
import android.os.Build

class BatteryMonitor(private val context: Context) {
    
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    val phoneBatteryPct: Int
        get() {
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        }

    /**
     * Intenta obtener la batería del casco Bluetooth (no es universal).
     */
    fun tryGetHeadsetBattery(): Int? {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) return null

        try {
            // Obtenemos los dispositivos vinculados
            // Nota: En Android 12+ (API 31) esto requiere el permiso BLUETOOTH_CONNECT
            val pairedDevices = bluetoothAdapter.bondedDevices
            if (pairedDevices.isNullOrEmpty()) return null

            // Buscamos el dispositivo que tenga un nivel de batería reportado
            // Generalmente es el que está conectado actualmente como Headset
            for (device in pairedDevices) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    // Usamos reflexión simple o llamada directa si el SDK lo permite
                    // En algunos entornos de compilación, getBatteryLevel() puede dar problemas de visibilidad
                    val level = try {
                        device.javaClass.getMethod("getBatteryLevel").invoke(device) as Int
                    } catch (e: Exception) {
                        -1 // BATTERY_LEVEL_UNKNOWN
                    }
                    
                    if (level != -1 && level >= 0) {
                        return level
                    }
                }
            }
        } catch (e: SecurityException) {
            // Falta permiso BLUETOOTH_CONNECT
        } catch (e: Exception) {
            // Otros errores silenciosos
        }

        return null
    }
}
