package com.uriel.logpose.thamis.voice.recognition

import java.util.*

/**
 * Normaliza el texto recibido del motor de STT.
 */
object SpeechNormalizer {

    fun normalize(text: String): String {
        return text.lowercase(Locale.getDefault())
            .trim()
            .replace(Regex("[¿?¡!\\.,]"), "")
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")
            .replace("ü", "u")
    }
}
