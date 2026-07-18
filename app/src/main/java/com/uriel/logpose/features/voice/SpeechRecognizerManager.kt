package com.uriel.logpose.features.voice

import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale

class SpeechRecognizerManager(
    private val context: Context
) {

    private var recognizer: SpeechRecognizer? = null

    fun start(
        listener: RecognitionListener
    ) {

        if (recognizer == null) {

            recognizer =
                SpeechRecognizer.createSpeechRecognizer(
                    context
                )

            recognizer?.setRecognitionListener(
                listener
            )
        }

        recognizer?.startListening(
            createIntent()
        )
    }

    fun stop() {
        recognizer?.stopListening()
    }

    fun cancel() {
        recognizer?.cancel()
    }

    fun destroy() {

        recognizer?.destroy()

        recognizer = null
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
                Locale.getDefault()
            )

            putExtra(
                RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                true
            )

            putExtra(
                RecognizerIntent.EXTRA_MAX_RESULTS,
                3
            )
        }
    }
}