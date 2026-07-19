package com.uriel.logpose.features.bluetooth


import android.Manifest
import androidx.annotation.RequiresPermission
import com.uriel.logpose.domain.models.LogPoseDevice



class BluetoothSessionManager(
    private val repository: BluetoothRepository
) {



    private var bluetoothEnabled =
        false







    @RequiresPermission(
        Manifest.permission.BLUETOOTH_CONNECT
    )
    fun refresh() {


        bluetoothEnabled =
            repository.isBluetoothEnabled()


    }









    @RequiresPermission(
        Manifest.permission.BLUETOOTH_CONNECT
    )
    fun buildState(): BluetoothState {


        val devices =
            repository.getPairedDevices()





        return BluetoothState(


            bluetoothEnabled =
                bluetoothEnabled,



            discovering =
                false,



            devices =
                devices,



            selectedDevice =
                null,



            connected =
                repository.isConnected(),



            error =
                null


        )


    }









    @RequiresPermission(
        Manifest.permission.BLUETOOTH_SCAN
    )
    fun startDiscovery(

        onFound: (LogPoseDevice) -> Unit,

        onFinished: () -> Unit

    ) {


        repository.startDiscovery(


            onDeviceFound = { device ->


                onFound(
                    device
                )


            },



            onFinished = {


                onFinished()


            }


        )


    }









    @RequiresPermission(
        Manifest.permission.BLUETOOTH_CONNECT
    )
    suspend fun connect(
        device: LogPoseDevice
    ): Boolean {


        return repository.connectDevice(
            device
        )


    }









    fun disconnect() {


        repository.disconnectDevice()


    }









    fun isConnected(): Boolean {


        return repository.isConnected()


    }



}