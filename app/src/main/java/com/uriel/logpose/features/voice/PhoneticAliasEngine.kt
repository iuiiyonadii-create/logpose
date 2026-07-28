package com.uriel.logpose.features.voice

import com.uriel.logpose.thamis.language.PhoneticEngine

/**
 * PhoneticAliasEngine: Mapea alucinaciones de Vosk a entidades reales.
 */
object PhoneticAliasEngine {

    private val ALIAS_MAP = mapOf(
        "duque" to "duki",
        "duke" to "duki",
        "tuki" to "duki",
        "ysya" to "ysy a",
        "ysy" to "ysy a",
        "soda" to "soda stereo",
        "rock" to "rockstar",
        "rocks" to "rockstar"
    )

    fun resolve(voskOutput: String): String? {
        val normalized = MusicVocabulary.normalize(voskOutput)
        
        // 1. Match directo
        if (ALIAS_MAP.containsKey(normalized)) return ALIAS_MAP[normalized]

        // 2. Match por similitud fonética
        for ((alias, target) in ALIAS_MAP) {
            if (PhoneticEngine.similarity(normalized, alias) > 0.85f) {
                return target
            }
        }

        return null
    }
}
