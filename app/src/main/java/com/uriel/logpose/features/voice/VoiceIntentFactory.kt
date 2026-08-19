package com.uriel.logpose.features.voice

import android.content.Intent
import android.speech.RecognizerIntent


/**
 * Crea el Intent de reconocimiento de voz parametrizado
 * según la VoiceConfiguration activa. Reemplaza el Intent
 * hardcodeado que existía en SpeechRecognizerManager.
 */
object VoiceIntentFactory {


    fun create(
        config: VoiceConfiguration
    ): Intent {

        return Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        ).apply {

            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                resolveLanguageModel(config.languageModel)
            )

            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                config.language
            )

            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                config.language
            )

            putExtra(
                RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                config.partialResults
            )

            putExtra(
                RecognizerIntent.EXTRA_MAX_RESULTS,
                config.maxResults
            )
        }
    }


    private fun resolveLanguageModel(
        model: String
    ): String {

        return when (model) {
            "web_search" ->
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
            else ->
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        }
    }
}
