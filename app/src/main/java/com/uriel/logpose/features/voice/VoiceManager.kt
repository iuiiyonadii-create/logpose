package com.uriel.logpose.features.voice

import com.uriel.logpose.core.commands.CommandProcessor
import com.uriel.logpose.core.compat.core.Command
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.engine.CommandDispatcher


object VoiceManager {


    private lateinit var voiceRepository: VoiceRepository



    fun initialize(
        repository: VoiceRepository
    ) {

        voiceRepository = repository


        LogPoseLogger.i(
            "VoiceManager inicializado"
        )

    }





    fun start() {


        if (
            voiceRepository.current()
            == VoiceState.LISTENING
        ) {

            return

        }



        voiceRepository.startListening()



        LogPoseLogger.i(
            "VoiceManager iniciado"
        )



        CommandDispatcher.execute(
            Command.StartListening
        )

    }





    fun stop() {


        if (
            voiceRepository.current()
            != VoiceState.LISTENING
        ) {

            return

        }



        voiceRepository.stopListening()



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


        if (
            voiceRepository.current()
            != VoiceState.LISTENING
        ) {

            return

        }





        LogPoseLogger.i(
            "Texto recibido: $text"
        )





        voiceRepository.processing()





        val command =
            CommandProcessor.process(
                text
            )





        CommandDispatcher.execute(
            command
        )





        if (
            command != Command.Unknown
        ) {

            voiceRepository.startListening()

        } else {

            voiceRepository.error()

        }

    }





    fun isListening(): Boolean {

        return (
                voiceRepository.current()
                        == VoiceState.LISTENING
                )

    }



}