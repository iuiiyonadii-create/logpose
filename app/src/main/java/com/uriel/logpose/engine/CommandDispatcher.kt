package com.uriel.logpose.engine

import com.uriel.logpose.compat.core.Command
import com.uriel.logpose.compat.core.LogPoseLogger

object CommandDispatcher {

    fun execute(command: Command) {

        LogPoseLogger.i("Procesando: $command")

        when (command) {

            Command.StartListening -> {
                LogPoseEngine.startListening()
            }

            Command.StopListening -> {
                LogPoseEngine.stopListening()
            }

            is Command.Navigate -> {
                LogPoseLogger.i("Destino: ${command.destination}")
            }

            is Command.PlayMusic -> {
                LogPoseLogger.i("App música: ${command.app}")
            }

            is Command.Call -> {
                LogPoseLogger.i("Llamar a: ${command.contact}")
            }

            Command.Unknown -> {
                LogPoseLogger.w("Comando desconocido")
            }

        }

    }

}