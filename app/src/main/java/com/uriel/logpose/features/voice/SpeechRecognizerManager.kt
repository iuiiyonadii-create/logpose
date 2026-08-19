package com.uriel.logpose.features.voice

import android.content.Context
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import com.uriel.logpose.core.compat.core.LogPoseLogger


class SpeechRecognizerManager(
    private val context: Context
) {


    private var recognizer: SpeechRecognizer? = null
    private var currentConfig: VoiceConfiguration? = null



    fun initialize(
        listener: RecognitionListener,
        config: VoiceConfiguration = VoiceConfiguration()
    ) {

        // Si ya existe un recognizer con otra config, destruirlo primero
        if (recognizer != null && currentConfig != config) {
            destroy()
        }

        if (recognizer != null) return


        currentConfig = config


        recognizer = SpeechRecognizerFactory.create(
            context, config
        )


        recognizer?.setRecognitionListener(
            listener
        )


        LogPoseLogger.i(
            "SpeechRecognizer inicializado" +
                    (config.engineComponent?.let {
                        " con motor: ${it.packageName}"
                    } ?: " con motor predeterminado")
        )

    }



    fun start() {

        val config = currentConfig

        if (recognizer == null || config == null) {

            LogPoseLogger.w(
                "SpeechRecognizer no inicializado"
            )

            return
        }


        val intent = VoiceIntentFactory.create(config)

        recognizer?.startListening(intent)


        LogPoseLogger.i(
            "Escucha de voz iniciada (idioma: ${config.language})"
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
        currentConfig = null

        LogPoseLogger.i(
            "SpeechRecognizer destruido"
        )

    }


    /**
     * Recrea el recognizer con una nueva configuración.
     * Necesario cuando el usuario cambia de motor de voz.
     */
    fun reconfigure(
        listener: RecognitionListener,
        config: VoiceConfiguration
    ) {
        destroy()
        initialize(listener, config)
    }

}