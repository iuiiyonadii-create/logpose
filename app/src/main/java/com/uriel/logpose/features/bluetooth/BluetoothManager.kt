package com.uriel.logpose.features.bluetooth


import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.util.Log
import androidx.annotation.RequiresPermission
import android.content.Context
import com.uriel.logpose.core.compat.core.DeviceClassifier
import com.uriel.logpose.domain.models.LogPoseDevice



class BluetoothManager(
    context: Context
) {



    private val appContext =
        context.applicationContext



    @Suppress("DEPRECATION")
    private val bluetoothAdapter =
        BluetoothAdapter.getDefaultAdapter()







    fun isBluetoothAvailable(): Boolean {

        return bluetoothAdapter != null

    }








    @RequiresPermission(
        anyOf = [
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH
        ]
    )
    @Suppress("MissingPermission")
    fun isBluetoothEnabled(): Boolean {


        val adapter =
            bluetoothAdapter



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









    @RequiresPermission(
        anyOf = [
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH
        ]
    )
    @Suppress("MissingPermission")
    fun getPairedDevices(): List<LogPoseDevice> {



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









    @RequiresPermission(
        anyOf = [
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH
        ]
    )
    @Suppress("MissingPermission")
    fun startDiscovery(): Boolean {



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









    @RequiresPermission(
        anyOf = [
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH
        ]
    )
    @Suppress("MissingPermission")
    fun cancelDiscovery() {



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


}