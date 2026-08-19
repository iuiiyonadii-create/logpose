package com.uriel.logpose.features.voice

import android.speech.SpeechRecognizer


/**
 * Errores de voz tipificados. Traduce los códigos
 * enteros de SpeechRecognizer a causas concretas
 * para permitir recuperación automática diferenciada.
 */
enum class VoiceError(
    val recoverable: Boolean
) {

    /** No se detectó entrada de voz del usuario */
    NO_MATCH(recoverable = true),

    /** Timeout sin voz detectada */
    SPEECH_TIMEOUT(recoverable = true),

    /** Error de red (reconocimiento requiere internet) */
    NETWORK(recoverable = true),

    /** Error de red con timeout */
    NETWORK_TIMEOUT(recoverable = true),

    /** El servidor devolvió un error */
    SERVER(recoverable = true),

    /** Falta permiso RECORD_AUDIO */
    PERMISSION_DENIED(recoverable = false),

    /** Otro cliente ya está usando el micrófono */
    RECOGNIZER_BUSY(recoverable = true),

    /** Audio insuficiente capturado */
    AUDIO(recoverable = true),

    /** El cliente generó un error */
    CLIENT(recoverable = false),

    /** Error desconocido */
    UNKNOWN(recoverable = false);


    companion object {

        fun from(errorCode: Int): VoiceError {
            return when (errorCode) {
                SpeechRecognizer.ERROR_NO_MATCH -> NO_MATCH
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> SPEECH_TIMEOUT
                SpeechRecognizer.ERROR_NETWORK -> NETWORK
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> NETWORK_TIMEOUT
                SpeechRecognizer.ERROR_SERVER -> SERVER
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> PERMISSION_DENIED
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> RECOGNIZER_BUSY
                SpeechRecognizer.ERROR_AUDIO -> AUDIO
                SpeechRecognizer.ERROR_CLIENT -> CLIENT
                else -> UNKNOWN
            }
        }
    }
}
