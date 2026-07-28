package com.uriel.logpose.features.voice

import android.content.Context
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.features.music.MusicManager

/**
 * SlotInterceptor V4: Detección de números con debouncer integrado.
 */
class SlotInterceptor(context: Context) {
    private val slots = VoiceSlotManager(context)
    private val debouncer = SlotDebouncer()

    fun intercept(text: String): Boolean {
        val t = MusicVocabulary.normalize(text)
        val n = extractNumber(t)
        
        if (n != null) {
            if (!debouncer.shouldExecute(n)) {
                LogPoseLogger.d("SlotInterceptor: Duplicado descartado para slot $n")
                return true
            }

            val slot = slots.getSlot(n)
            if (slot != null && slot.spotifyQuery.isNotBlank()) {
                LogPoseLogger.i("SlotInterceptor: 🎯 EJECUTANDO SLOT $n -> '${slot.spotifyQuery}'")
                MusicManager.play(slot.spotifyQuery)
                return true
            }
            
            LogPoseLogger.w("SlotInterceptor: Se detectó el número $n pero el slot está vacío.")
            return true 
        }

        return false
    }

    private fun extractNumber(t: String): Int? {
        val words = t.split(Regex("\\s+"))
        return when {
            words.any { it == "uno" || it == "one" } -> 1
            words.any { it == "dos" || it == "two" } -> 2
            words.any { it == "tres" || it == "three" } -> 3
            words.any { it == "cuatro" || it == "four" } -> 4
            words.any { it == "cinco" || it == "five" } -> 5
            words.any { it == "seis" || it == "six" } -> 6
            words.any { it == "siete" || it == "seven" } -> 7
            words.any { it == "ocho" || it == "eight" } -> 8
            words.any { it == "nueve" || it == "nine" } -> 9
            words.any { it == "diez" || it == "ten" } -> 10
            else -> null
        }
    }
}
