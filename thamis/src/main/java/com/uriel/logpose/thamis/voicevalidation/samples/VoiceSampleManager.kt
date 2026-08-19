package com.uriel.logpose.thamis.voicevalidation.samples

import com.uriel.logpose.thamis.voicevalidation.model.VoiceSample

/**
 * Gestión de ejemplos de voz y dataset humano para validación.
 */
object VoiceSampleManager {

    private val dataset = mutableListOf<VoiceSample>()

    init {
        // Registro de frases comunes y variaciones argentinas/rioplatenses
        register("poneme música", "PLAY_MUSIC", "argentino", "IDLE")
        register("pone música", "PLAY_MUSIC", "rioplatense", "IDLE")
        register("quiero música", "PLAY_MUSIC", "informal", "IDLE")
        register("dale música", "PLAY_MUSIC", "informal", "IDLE")
        register("subí volumen", "INCREASE_VOLUME", "argentino", "MUSIC_ACTIVE")
        register("más fuerte", "INCREASE_VOLUME", "natural", "MUSIC_ACTIVE")
        register("bajalo", "DECREASE_VOLUME", "informal", "MUSIC_ACTIVE")
        register("llamame a mama", "CALL_CONTACT", "natural", "IDLE")
        register("mandale mensaje", "SEND_MESSAGE", "natural", "IDLE")
    }

    private fun register(phrase: String, intent: String, variation: String, context: String) {
        dataset.add(VoiceSample(phrase = phrase, expectedIntent = intent, variation = variation, context = context))
    }

    fun getSamples(): List<VoiceSample> = dataset.toList()
    
    fun getSamplesByVariation(variation: String): List<VoiceSample> {
        return dataset.filter { it.variation == variation }
    }
}
