package com.uriel.logpose.features.bluetooth


import com.uriel.logpose.core.compat.core.LogPoseLogger
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


        LogPoseLogger.d(
            "LOGPOSE_BT",
            "SESSION DISCONNECTED"
        )


    }






    suspend fun connect(
        device: LogPoseDevice
    ): Boolean {


        LogPoseLogger.d(
            "LOGPOSE_BT",
            "SESSION CONNECT ${device.name}"
        )



        return bluetoothRepository
            .connectDevice(
                device
            )


    }



}