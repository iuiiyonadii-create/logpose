package com.uriel.logpose.features.voice

import com.uriel.logpose.core.commands.CommandProcessor
import com.uriel.logpose.core.engine.CommandDispatcher
import com.uriel.logpose.core.compat.core.Command
import com.uriel.logpose.core.compat.core.LogPoseLogger


object VoiceManager {


    private var listening = false


    private var voiceRepository: VoiceRepository? = null



    fun initialize(
        repository: VoiceRepository
    ) {

        voiceRepository = repository


        LogPoseLogger.i(
            "VoiceManager inicializado"
        )

    }





    fun start() {


        if (listening) return



        if (voiceRepository == null) {

            LogPoseLogger.w(
                "VoiceRepository no inicializado"
            )

            return
        }



        listening = true



        voiceRepository?.startListening()



        LogPoseLogger.i(
            "VoiceManager iniciado"
        )



        CommandDispatcher.execute(
            Command.StartListening
        )

    }





    fun stop() {


        if (!listening) return



        listening = false



        voiceRepository?.stopListening()



        LogPoseLogger.i(
            "VoiceManager detenido"
        )



        CommandDispatcher.execute(
            Command.StopListening
        )

    }





    fun onTextReceived(
        text: String
    ) {


        if (!listening) return



        LogPoseLogger.i(
            "Texto recibido: $text"
        )



        val command =
            CommandProcessor.process(
                text
            )



        CommandDispatcher.execute(
            command
        )

    }





    fun isListening(): Boolean =

        listening

}