package com.uriel.logpose.core.engine

import com.uriel.logpose.core.compat.core.Command
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.context.CommandContext
import com.uriel.logpose.core.context.CommandHistory
import com.uriel.logpose.core.engine.registry.DefaultCommandRegistry
import com.uriel.logpose.core.memory.CommandMemory

object CommandDispatcher {

    private val registry = DefaultCommandRegistry()

    init {

        registry.register(Command.StartListening::class) {
            LogPoseEngine.startListening()
        }

        registry.register(Command.StopListening::class) {
            LogPoseEngine.stopListening()
        }

        registry.register(Command.Navigate::class) { command ->
            val navigate = command as Command.Navigate
            LogPoseLogger.i("Destino: ${navigate.destination}")
        }

        registry.register(Command.PlayMusic::class) { command ->
            val music = command as Command.PlayMusic
            LogPoseLogger.i("App música: ${music.app}")
        }

        registry.register(Command.Call::class) { command ->
            val call = command as Command.Call
            LogPoseLogger.i("Llamar a: ${call.contact}")
        }

        registry.register(Command.Unknown::class) {
            LogPoseLogger.w("Comando desconocido")
        }
    }

    fun execute(command: Command) {

        LogPoseLogger.i("Procesando: $command")

        val executed = registry.execute(command)

        CommandHistory.add(
            CommandContext(
                command = command,
                success = executed
            )
        )

        CommandMemory.remember(command)

        if (!executed) {
            LogPoseLogger.w(
                "No existe handler para ${command::class.simpleName}"
            )
        }
    }
}