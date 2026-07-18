package com.uriel.logpose.features.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.uriel.logpose.core.compat.core.LogPoseLogger
import java.util.Locale


class SpeechRecognizerManager(
    private val context: Context
) {


    private var recognizer: SpeechRecognizer? = null



    fun initialize(
        listener: RecognitionListener
    ) {


        if (recognizer != null) return



        recognizer =
            SpeechRecognizer.createSpeechRecognizer(
                context
            )


        recognizer?.setRecognitionListener(
            listener
        )


        LogPoseLogger.i(
            "SpeechRecognizer inicializado"
        )

    }




    fun start() {


        if (recognizer == null) {

            LogPoseLogger.w(
                "SpeechRecognizer no inicializado"
            )

            return
        }



        recognizer?.startListening(
            createIntent()
        )


        LogPoseLogger.i(
            "Escucha de voz iniciada"
        )

    }





    fun stop() {


        recognizer?.stopListening()


        LogPoseLogger.i(
            "Escucha detenida"
        )

    }





    fun cancel() {


        recognizer?.cancel()


        LogPoseLogger.i(
            "Escucha cancelada"
        )

    }





    fun destroy() {


        recognizer?.destroy()

        recognizer = null


        LogPoseLogger.i(
            "SpeechRecognizer destruido"
        )

    }





    private fun createIntent(): Intent {


        return Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        ).apply {


            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )



            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                "es-AR"
            )



            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "es-AR"
            )



            putExtra(
                RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                true
            )



            putExtra(
                RecognizerIntent.EXTRA_MAX_RESULTS,
                5
            )


        }

    }

}