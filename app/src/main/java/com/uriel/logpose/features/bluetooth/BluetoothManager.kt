package com.uriel.logpose.features.bluetooth


import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager as AndroidBluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.uriel.logpose.core.compat.core.DeviceClassifier
import com.uriel.logpose.domain.models.LogPoseDevice



class BluetoothManager(
    context: Context
) {



    private val appContext =
        context.applicationContext



    // minSdk actual no soporta BluetoothManager.adapter sin Context, requiere minSdk 31 para inyección directa
    @Suppress("DEPRECATION")
    private val bluetoothAdapter: BluetoothAdapter? =
        (appContext.getSystemService(Context.BLUETOOTH_SERVICE) as? AndroidBluetoothManager)?.adapter
            ?: BluetoothAdapter.getDefaultAdapter()







    fun isBluetoothAvailable(): Boolean {

        return bluetoothAdapter != null

    }








    fun isBluetoothEnabled(): Boolean {

        val adapter =
            bluetoothAdapter

        if (!hasConnectPermission()) {
            Log.w("LOGPOSE_BT", "Permiso Bluetooth no concedido para verificar isEnabled")
            return false
        }

        Log.d(
            "LOGPOSE_BT",
            "Adapter = $adapter"
        )



        Log.d(
            "LOGPOSE_BT",
            "Enabled = ${adapter?.isEnabled}"
        )



        Log.d(
            "LOGPOSE_BT",
            "State = ${adapter?.state}"
        )



        return adapter?.isEnabled == true


    }










    fun getPairedDevices(): List<LogPoseDevice> {

        if (!hasConnectPermission()) {
            Log.w("LOGPOSE_BT", "Permiso Bluetooth no concedido para getPairedDevices")
            return emptyList()
        }

        val devices =
            bluetoothAdapter
                ?.bondedDevices
                .orEmpty()





        Log.d(
            "LOGPOSE_BT",
            "Bonded devices = ${devices.size}"
        )





        return devices
            .map { device ->



                Log.d(
                    "LOGPOSE_BT",
                    "Device: ${device.name} (${device.address})"
                )



                LogPoseDevice(

                    mac = device.address,

                    name = device.name
                        ?: "Desconocido",


                    type =
                        DeviceClassifier.detect(
                            device.name.orEmpty()
                        ),


                    connected = false

                )


            }
            .sortedBy {

                it.name

            }



    }










    fun startDiscovery(): Boolean {

        if (!hasScanPermission()) {
            Log.w("LOGPOSE_BT", "Permiso Bluetooth SCAN no concedido para startDiscovery")
            return false
        }

        val adapter =
            bluetoothAdapter
                ?: return false





        Log.d(
            "LOGPOSE_BT",
            "Adapter enabled = ${adapter.isEnabled}"
        )





        Log.d(
            "LOGPOSE_BT",
            "Discovering before = ${adapter.isDiscovering}"
        )





        if (adapter.isDiscovering) {


            Log.d(
                "LOGPOSE_BT",
                "Discovery already running"
            )


            return true

        }







        Log.d(
            "LOGPOSE_BT",
            "Starting discovery..."
        )





        val result =
            adapter.startDiscovery()





        Log.d(
            "LOGPOSE_BT",
            "DISCOVERY RESULT = $result"
        )





        Log.d(
            "LOGPOSE_BT",
            "Discovering after = ${adapter.isDiscovering}"
        )




        return result



    }










    fun cancelDiscovery() {

        if (!hasScanPermission()) {
            return
        }

        val adapter =
            bluetoothAdapter
                ?: return





        if (adapter.isDiscovering) {



            Log.d(
                "LOGPOSE_BT",
                "Cancel discovery"
            )



            adapter.cancelDiscovery()


        }


    }

    private fun hasConnectPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun hasScanPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

}