package com.uriel.logpose.features.voice

import android.content.Context
import android.speech.SpeechRecognizer
import com.uriel.logpose.core.compat.core.LogPoseLogger


/**
 * Crea instancias de SpeechRecognizer respetando el
 * motor seleccionado en VoiceConfiguration.
 *
 * Si engineComponent es null, usa el motor predeterminado del SO.
 * Si engineComponent tiene un ComponentName válido, crea el
 * recognizer apuntando a ese motor específico.
 */
object SpeechRecognizerFactory {


    fun create(
        context: Context,
        config: VoiceConfiguration
    ): SpeechRecognizer {

        val component = config.engineComponent

        return if (component != null) {

            LogPoseLogger.i(
                "Creando SpeechRecognizer con motor: " +
                        "${component.packageName}/${component.className}"
            )

            SpeechRecognizer.createSpeechRecognizer(
                context,
                component
            )

        } else {

            LogPoseLogger.i(
                "Creando SpeechRecognizer con motor predeterminado del sistema"
            )

            SpeechRecognizer.createSpeechRecognizer(
                context
            )
        }
    }
}
