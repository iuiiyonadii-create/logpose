package com.uriel.logpose.features.voice

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import com.uriel.logpose.core.compat.core.LogPoseLogger


class SpeechRecognitionListener(
    private val onTextReceived: (String) -> Unit,
    private val onReady: () -> Unit = {},
    private val onError: (Int) -> Unit = {}
) : RecognitionListener {



    override fun onReadyForSpeech(
        params: Bundle?
    ) {

        LogPoseLogger.i(
            "Micrófono preparado"
        )

        onReady()
    }




    override fun onBeginningOfSpeech() {

        LogPoseLogger.i(
            "Usuario hablando"
        )

    }




    override fun onRmsChanged(
        rmsdB: Float
    ) {

    }




    override fun onBufferReceived(
        buffer: ByteArray?
    ) {

    }




    override fun onEndOfSpeech() {

        LogPoseLogger.i(
            "Fin de voz"
        )

    }




    override fun onError(
        error: Int
    ) {

        LogPoseLogger.w(
            "Error reconocimiento: $error"
        )

        onError(error)

    }




    override fun onResults(
        results: Bundle?
    ) {


        val matches =
            results?.getStringArrayList(
                SpeechRecognizer.RESULTS_RECOGNITION
            )



        val text =
            matches
                ?.firstOrNull()
                ?: return



        LogPoseLogger.i(
            "Texto reconocido: $text"
        )


        onTextReceived(
            text
        )

    }




    override fun onPartialResults(
        partialResults: Bundle?
    ) {


        val matches =
            partialResults?.getStringArrayList(
                SpeechRecognizer.RESULTS_RECOGNITION
            )


        val text =
            matches
                ?.firstOrNull()
                ?: return



        LogPoseLogger.i(
            "Parcial: $text"
        )

    }




    override fun onEvent(
        eventType: Int,
        params: Bundle?
    ) {

    }

}