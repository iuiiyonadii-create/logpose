package com.uriel.logpose.thamis.voicevalidation.phonetic

import com.uriel.logpose.thamis.voice.phonetic.PhoneticAnalyzer

/**
 * Valida la robustez fonética del sistema ante diferentes pronunciaciones.
 */
object PhoneticValidationEngine {

    /**
     * Mapeo de pronunciaciones comunes o erróneas a términos canónicos.
     */
    private val phoneticSlang = mapOf(
        "espotifai" to "spotify",
        "spoty" to "spotify",
        "espoty" to "spotify",
        "guasa" to "whatsapp",
        "guasap" to "whatsapp",
        "feisbu" to "facebook",
        "yutu" to "youtube"
    )

    fun validateSlang(input: String): String {
        val words = input.split(" ")
        return words.joinToString(" ") { word ->
            phoneticSlang[word.lowercase()] ?: PhoneticAnalyzer.getCanonical(word)
        }
    }

    /**
     * Evalúa si dos frases son fonéticamente equivalentes en el contexto LogPose.
     */
    fun areEquivalent(phrase1: String, phrase2: String): Boolean {
        return validateSlang(phrase1) == validateSlang(phrase2)
    }
}
