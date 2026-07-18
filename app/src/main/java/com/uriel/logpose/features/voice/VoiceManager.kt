package com.uriel.logpose.features.voice

import com.uriel.logpose.core.compat.core.Command
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.engine.CommandDispatcher
import com.uriel.logpose.core.engine.LogPoseEngine

object VoiceManager {

    private var listening = false

    fun start() {

        if (listening) return

        listening = true

        LogPoseLogger.i("VoiceManager iniciado")

        CommandDispatcher.execute(
            Command.StartListening
        )
    }

    fun stop() {

        if (!listening) return

        listening = false

        LogPoseLogger.i("VoiceManager detenido")

        CommandDispatcher.execute(
            Command.StopListening
        )
    }

    fun onTextReceived(text: String) {

        if (!listening) return

        LogPoseLogger.i("Texto recibido: $text")

        LogPoseEngine.processCommand(text)
    }

    fun isListening(): Boolean = listening
}