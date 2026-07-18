package com.uriel.logpose.features.voice

import kotlinx.coroutines.flow.StateFlow


class VoiceRepository(

    private val session: VoiceSession = VoiceSession()

) {


    private var speechRecognizerManager: SpeechRecognizerManager? = null



    val state: StateFlow<VoiceState>
        get() = session.state





    fun attachRecognizer(
        manager: SpeechRecognizerManager
    ) {

        speechRecognizerManager = manager


        manager.initialize(

            SpeechRecognitionListener(

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


                    session.update(
                        VoiceState.ERROR
                    )


                }

            )

        )

    }





    fun initialize() {

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

}