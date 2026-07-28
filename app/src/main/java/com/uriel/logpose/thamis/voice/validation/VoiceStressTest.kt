package com.uriel.logpose.thamis.voice.validation

import com.uriel.logpose.thamis.voice.engine.VoiceIntelligenceEngine

/**
 * Suite de simulación para validar la robustez de la comprensión vocal.
 */
class VoiceStressTest {

    fun runScenario() {
        val inputs = listOf(
            "Poneme música" to 0.9f,
            "subi" to 0.7f,
            "Llamá a Juan Perez" to 0.95f,
            "donde estoy" to 0.8f,
            "no entiendo nada" to 0.4f
        )

        inputs.forEach { (text, confidence) ->
            VoiceIntelligenceEngine.processVoice(text, confidence)
        }
    }
}
