package com.uriel.logpose.logprobe.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import com.uriel.logpose.logprobe.common.ProbeClock
import com.uriel.logpose.logprobe.models.BluetoothSnapshot

/**
 * Lectura de estado de Bluetooth usando unicamente APIs publicas.
 * No escanea, no se conecta a nada, no interactua con Bluetooth: solo
 * lee el estado del adaptador y los dispositivos ya conectados via
 * BluetoothManager.getConnectedDevices, que requiere el permiso
 * BLUETOOTH_CONNECT (Android 12+) pero ningun permiso de ubicacion.
 */
class BluetoothProbe(context: Context) {

    private val bluetoothManager =
        context.applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val adapter: BluetoothAdapter? = bluetoothManager?.adapter

    @SuppressLint("MissingPermission")
    fun currentSnapshot(): BluetoothSnapshot {
        val enabled = adapter?.isEnabled == true
        // NOTA: getConnectedDevices requiere el perfil especifico (GATT,
        // A2DP, HEADSET, etc). Se consultan los mas comunes; esto es
        // best-effort y puede no cubrir todos los perfiles posibles.
        val profilesToCheck = listOf(BluetoothProfile.GATT, BluetoothProfile.HEADSET, BluetoothProfile.A2DP)
        val deviceNames = if (enabled) {
            try {
                profilesToCheck
                    .flatMap { profile -> bluetoothManager?.getConnectedDevices(profile).orEmpty() }
                    .mapNotNull { it.name }
                    .distinct()
            } catch (_: SecurityException) {
                emptyList()
            }
        } else {
            emptyList()
        }

        return BluetoothSnapshot(
            timestampMillis = ProbeClock.nowMillis(),
            isEnabled = enabled,
            connectedDeviceNames = deviceNames
        )
    }
}
