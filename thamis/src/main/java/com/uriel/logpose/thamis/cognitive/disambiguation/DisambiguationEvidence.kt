package com.uriel.logpose.thamis.cognitive.disambiguation

/**
 * Evidencias utilizadas para resolver ambigüedades entre entidades.
 */
data class DisambiguationEvidence(
    val type: Type,
    val impact: Float,
    val description: String
) {
    enum class Type {
        MUSIC_PLAYING,
        APP_INSTALLED,
        USER_HISTORY,
        PHONETIC_MATCH,
        FAVORITE_ENTITY,
        RECENT_ACTION,
        VOICE_CONFIDENCE
    }
}
