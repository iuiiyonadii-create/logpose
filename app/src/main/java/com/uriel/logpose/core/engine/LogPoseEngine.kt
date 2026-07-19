package com.uriel.logpose.core.engine


import com.uriel.logpose.core.compat.core.AppState
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.domain.models.LogPoseDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow



object LogPoseEngine {



    private val _state =
        MutableStateFlow(
            AppState.STOPPED
        )



    val state: StateFlow<AppState> =
        _state





    private var currentDevice: LogPoseDevice? =
        null









    fun onBluetoothConnected(
        device: LogPoseDevice
    ) {



        currentDevice =
            device



        LogPoseLogger.i(
            "Bluetooth conectado: ${device.name}"
        )



        _state.value =
            AppState.READY



    }









    fun onBluetoothDisconnected(
        device: LogPoseDevice
    ) {



        if(currentDevice?.mac != device.mac){
            return
        }



        currentDevice = null



        _state.value =
            AppState.STOPPED



        LogPoseLogger.i(
            "Bluetooth desconectado"
        )



    }









    fun startListening(){



        if(
            _state.value != AppState.READY
        ){
            return
        }





        _state.value =
            AppState.LISTENING




        LogPoseLogger.i(
            "Escuchando..."
        )



    }









    fun stopListening(){



        if(
            _state.value != AppState.LISTENING
        ){
            return
        }





        _state.value =
            AppState.READY



    }









    fun processing(){



        _state.value =
            AppState.PROCESSING



    }









    fun speaking(){



        _state.value =
            AppState.SPEAKING



    }









    fun ready(){



        _state.value =
            AppState.READY



    }









    fun stop(){



        currentDevice =
            null



        _state.value =
            AppState.STOPPED



        LogPoseLogger.i(
            "LogPose detenido"
        )



    }









    fun getState(): AppState {

        return _state.value

    }









    fun getCurrentDevice(): LogPoseDevice? {

        return currentDevice

    }



}