package com.uriel.logpose.core.engine

import com.uriel.logpose.core.compat.core.AppState
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.domain.models.LogPoseDevice


object LogPoseEngine {


    private var state =
        AppState.STOPPED



    private var currentDevice: LogPoseDevice? =
        null





    fun onBluetoothConnected(
        device: LogPoseDevice
    ) {


        currentDevice = device


        LogPoseLogger.i(
            "Bluetooth conectado: ${device.name}"
        )


        state =
            AppState.READY

    }





    fun onBluetoothDisconnected(
        device: LogPoseDevice
    ) {


        if (currentDevice?.mac != device.mac) {
            return
        }



        LogPoseLogger.i(
            "Bluetooth desconectado: ${device.name}"
        )



        currentDevice = null



        state =
            AppState.STOPPED

    }





    fun startListening() {


        if (
            state != AppState.READY
        ) {
            return
        }



        state =
            AppState.LISTENING



        LogPoseLogger.i(
            "Escuchando..."
        )

    }





    fun stopListening() {


        if (
            state != AppState.LISTENING
        ) {
            return
        }



        state =
            AppState.READY



        LogPoseLogger.i(
            "Escucha detenida"
        )

    }





    fun processing() {


        state =
            AppState.PROCESSING



        LogPoseLogger.i(
            "Procesando comando"
        )

    }





    fun speaking() {


        state =
            AppState.SPEAKING



        LogPoseLogger.i(
            "Hablando"
        )

    }





    fun ready() {


        state =
            AppState.READY



        LogPoseLogger.i(
            "Esperando comando"
        )

    }





    fun stop() {


        currentDevice = null



        state =
            AppState.STOPPED



        LogPoseLogger.i(
            "LogPose detenido"
        )

    }





    fun getState(): AppState =
        state





    fun getCurrentDevice(): LogPoseDevice? =
        currentDevice

}