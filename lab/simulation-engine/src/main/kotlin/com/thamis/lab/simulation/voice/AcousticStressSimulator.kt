package com.thamis.lab.simulation.voice

import com.thamis.lab.core.common.logging.LabLogger
import com.thamis.lab.core.common.telemetry.LabTelemetry

/**
 * AcousticStressSimulator: Simula condiciones extremas de audio para el motor de voz.
 */
public class AcousticStressSimulator {

    public data class StressProfile(
        val windSpeedKmh: Int,
        val noiseLevel: Float,
        val expectedAccuracy: Float
    )

    public fun simulateWindNoise(speed: Int): StressProfile {
        val noise = (speed.toFloat() / 150f).coerceAtMost(1.0f)
        val accuracy = 1.0f - (noise * 0.5f) // A más velocidad, menos precisión
        
        LabLogger.info("StressSim", "Simulating wind noise for $speed km/h (Noise: $noise)")
        LabTelemetry.recordMetric("sim_ambient_noise", (noise * 100).toLong())
        
        return StressProfile(speed, noise, accuracy)
    }

    public fun runPhoneticTest(transcription: String, target: String): Float {
        // Simulación de distancia de Levenshtein para medir precisión
        val s1 = transcription.lowercase().trim()
        val s2 = target.lowercase().trim()
        
        if (s1 == s2) return 1.0f
        
        val dist = levenshtein(s1, s2)
        val maxLen = maxOf(s1.length, s2.length)
        val score = 1.0f - (dist.toFloat() / maxLen.toFloat())
        
        LabTelemetry.logEvent("StressSim", "Phonetic Match Result: $score (Text: '$transcription' vs '$target')")
        return score
    }

    /**
     * Distorsiona un texto para simular ruidos de viento racheado o saturación.
     */
    public fun distortText(text: String, noiseLevel: Float): String {
        if (noiseLevel < 0.3f) return text
        
        val chars = text.toCharArray()
        for (i in chars.indices) {
            val rand = Math.random().toFloat()
            if (rand < (noiseLevel * 0.4f)) {
                // Simular alucinación fonética: intercambiar vocales similares
                when (chars[i]) {
                    'a' -> chars[i] = 'o'
                    'e' -> chars[i] = 'i'
                    'o' -> chars[i] = 'u'
                    'b' -> chars[i] = 'p'
                }
            }
        }
        val distorted = String(chars)
        LabLogger.info("StressSim", "Distorted '$text' to '$distorted' (Noise: $noiseLevel)")
        return distorted
    }

    private fun levenshtein(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[s1.length][s2.length]
    }
}
