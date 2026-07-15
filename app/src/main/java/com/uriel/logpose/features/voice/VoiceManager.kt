package com.uriel.logpose.features.voice

import com.uriel.logpose.core.compat.core.Command
import com.uriel.logpose.core.engine.CommandDispatcher
import com.uriel.logpose.core.compat.core.LogPoseLogger

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

    fun isListening(): Boolean = listening

}