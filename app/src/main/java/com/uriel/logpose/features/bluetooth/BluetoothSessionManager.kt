package com.uriel.logpose.features.bluetooth


import android.util.Log
import com.uriel.logpose.domain.repositories.BluetoothRepository
import com.uriel.logpose.domain.models.LogPoseDevice



class BluetoothSessionManager(
    private val bluetoothRepository: BluetoothRepository
) {



    fun isConnected(): Boolean {


        return bluetoothRepository
            .isConnected()


    }






    fun disconnect(){


        bluetoothRepository
            .disconnectDevice()


        Log.d(
            "LOGPOSE_BT",
            "SESSION DISCONNECTED"
        )


    }






    suspend fun connect(
        device: LogPoseDevice
    ): Boolean {


        Log.d(
            "LOGPOSE_BT",
            "SESSION CONNECT ${device.name}"
        )



        return bluetoothRepository
            .connectDevice(
                device
            )


    }



}