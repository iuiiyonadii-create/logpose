package com.uriel.logpose.core.engine

import com.uriel.logpose.core.compat.core.AppState
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.domain.models.LogPoseDevice
import com.uriel.logpose.core.parser.CommandParser
import com.uriel.logpose.core.parser.ParseResult

object LogPoseEngine {

    private var state = AppState.STOPPED

    private var currentDevice: LogPoseDevice? = null

    fun onBluetoothConnected(device: LogPoseDevice) {

        currentDevice = device

        LogPoseLogger.i("Bluetooth conectado: ${device.name}")

        state = AppState.READY
    }

    fun onBluetoothDisconnected(device: LogPoseDevice) {

        if (currentDevice?.mac != device.mac) return

        LogPoseLogger.i("Bluetooth desconectado: ${device.name}")

        currentDevice = null

        state = AppState.STOPPED
    }

    fun startListening() {

        if (state != AppState.READY) return

        state = AppState.LISTENING

        LogPoseLogger.i("Escuchando...")
    }

    fun stopListening() {

        if (state != AppState.LISTENING) return

        state = AppState.READY

        LogPoseLogger.i("Escucha detenida")
    }

    fun processing() {

        state = AppState.PROCESSING

        LogPoseLogger.i("Procesando comando")
    }

    fun speaking() {

        state = AppState.SPEAKING

        LogPoseLogger.i("Hablando")
    }

    fun ready() {

        state = AppState.READY

        LogPoseLogger.i("Esperando comando")
    }

    fun stop() {

        currentDevice = null

        state = AppState.STOPPED

        LogPoseLogger.i("LogPose detenido")
    }

    fun processCommand(text: String) {

        if (state != AppState.LISTENING) return

        processing()

        when (val result = CommandParser.parse(text)) {

            is ParseResult.Success -> {
                CommandDispatcher.execute(result.command)
            }

            ParseResult.Unknown -> {
                LogPoseLogger.w("Comando no reconocido: $text")
            }
        }

        ready()
    }

    fun getState(): AppState = state

    fun getCurrentDevice(): LogPoseDevice? = currentDevice
}