package com.uriel.logpose.features.voice

import com.uriel.logpose.core.compat.core.LogPoseLogger
import kotlinx.coroutines.flow.StateFlow


class VoiceRepository(

    private val session: VoiceSession = VoiceSession()

) {


    private var speechRecognizerManager: SpeechRecognizerManager? = null
    private var activeConfig: VoiceConfiguration = VoiceConfiguration()



    val state: StateFlow<VoiceState>
        get() = session.state





    fun attachRecognizer(
        manager: SpeechRecognizerManager
    ) {

        speechRecognizerManager = manager
    }



    fun initialize(
        config: VoiceConfiguration = VoiceConfiguration()
    ) {

        activeConfig = config

        val manager = speechRecognizerManager ?: return

        manager.initialize(
            createListener(),
            config
        )

        session.update(
            VoiceState.READY
        )
    }


    /**
     * Reconfigura el motor de voz con una nueva configuración.
     * Destruye el recognizer actual y crea uno nuevo.
     */
    fun reconfigure(
        config: VoiceConfiguration
    ) {

        val manager = speechRecognizerManager ?: return

        activeConfig = config

        manager.reconfigure(
            createListener(),
            config
        )

        LogPoseLogger.i(
            "VoiceRepository reconfigurado" +
                    (config.engineComponent?.let {
                        " con motor: ${it.packageName}"
                    } ?: " con motor predeterminado")
        )

        session.update(
            VoiceState.READY
        )
    }




    fun startListening() {


        session.update(
            VoiceState.LISTENING
        )


        speechRecognizerManager?.start()

    }





    fun processing() {


        session.update(
            VoiceState.PROCESSING
        )

    }





    fun stopListening() {


        speechRecognizerManager?.stop()



        session.update(
            VoiceState.STOPPED
        )

    }





    fun error() {


        session.update(
            VoiceState.ERROR
        )

    }





    fun reset() {


        session.update(
            VoiceState.IDLE
        )

    }





    fun destroy() {


        speechRecognizerManager?.destroy()


        speechRecognizerManager = null


        session.update(
            VoiceState.IDLE
        )

    }





    fun current(): VoiceState =

        session.current()



    private fun createListener(): SpeechRecognitionListener {

        return SpeechRecognitionListener(

            onTextReceived = { text ->

                VoiceManager.onTextReceived(
                    text
                )

            },


            onReady = {

                session.update(
                    VoiceState.READY
                )

            },


            onError = {
                // Mantiene compatibilidad con código existente
            },


            onTypedError = { voiceError ->

                if (voiceError.recoverable) {
                    LogPoseLogger.i(
                        "Error recuperable: ${voiceError.name}, reiniciando escucha"
                    )
                    session.update(VoiceState.READY)
                } else {
                    LogPoseLogger.w(
                        "Error no recuperable: ${voiceError.name}"
                    )
                    session.update(VoiceState.ERROR)
                }

            }

        )
    }

}