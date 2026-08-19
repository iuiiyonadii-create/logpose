package com.thamis.lab.core.common.speech

import com.thamis.lab.core.common.logging.LabLogger
import com.thamis.lab.core.common.telemetry.LabTelemetry

/**
 * AcousticStressSimulator: Simula condiciones extremas de audio para el motor de voz.
 * v71.0: Calibrated profiles for Motor, Wind, and Helmets.
 */
public class AcousticStressSimulator {

    public enum class AcousticScenario {
        SILENCE, URBAN, MOTOR_IDLE, MOTOR_HIGH_RPM, WIND_LIGHT, WIND_HEAVY, WIND_EXTREME,
        HELMET_CLOSED, HELMET_OPEN, RAIN, TRAFFIC_JAM
    }

    public data class StressProfile(
        val scenario: AcousticScenario,
        val noiseLevel: Float, // 0.0 to 1.0
        val snr: Double, // Real SNR estimate
        val expectedAccuracy: Float
    )

    public fun getCalibratedProfile(scenario: AcousticScenario): StressProfile {
        return when (scenario) {
            AcousticScenario.SILENCE -> StressProfile(scenario, 0.05f, 35.0, 0.99f)
            AcousticScenario.URBAN -> StressProfile(scenario, 0.35f, 15.0, 0.90f)
            AcousticScenario.MOTOR_IDLE -> StressProfile(scenario, 0.45f, 10.0, 0.85f)
            AcousticScenario.MOTOR_HIGH_RPM -> StressProfile(scenario, 0.65f, 6.0, 0.75f)
            AcousticScenario.WIND_LIGHT -> StressProfile(scenario, 0.40f, 12.0, 0.88f)
            AcousticScenario.WIND_HEAVY -> StressProfile(scenario, 0.75f, 4.0, 0.65f)
            AcousticScenario.WIND_EXTREME -> StressProfile(scenario, 0.90f, 1.5, 0.50f)
            AcousticScenario.HELMET_CLOSED -> StressProfile(scenario, 0.25f, 18.0, 0.94f)
            AcousticScenario.HELMET_OPEN -> StressProfile(scenario, 0.70f, 5.0, 0.70f)
            AcousticScenario.RAIN -> StressProfile(scenario, 0.50f, 10.0, 0.82f)
            AcousticScenario.TRAFFIC_JAM -> StressProfile(scenario, 0.40f, 14.0, 0.88f)
        }
    }

    public fun simulateWindNoise(speedKmh: Int): StressProfile {
        val scenario = when {
            speedKmh > 100 -> AcousticScenario.WIND_EXTREME
            speedKmh > 60 -> AcousticScenario.WIND_HEAVY
            else -> AcousticScenario.WIND_LIGHT
        }
        return getCalibratedProfile(scenario)
    }

    public fun distortText(text: String, noiseLevel: Float): String {
        if (noiseLevel < 0.3f) return text
        val chars = text.toCharArray()
        val random = java.util.Random(text.hashCode().toLong())
        for (i in chars.indices) {
            if (random.nextFloat() < noiseLevel * 0.35f && chars[i].isLetter()) {
                chars[i] = when (chars[i].lowercaseChar()) {
                    'p' -> 'b'
                    'b' -> 'p'
                    'd' -> 't'
                    't' -> 'd'
                    's' -> 'z'
                    'm' -> 'n'
                    'n' -> 'm'
                    else -> chars[i]
                }
            }
        }
        return String(chars)
    }

    public fun runPhoneticTest(transcription: String, target: String): Float {
        val s1 = transcription.lowercase().trim()
        val s2 = target.lowercase().trim()
        
        if (s1 == s2) return 1.0f
        
        val dist = levenshtein(s1, s2)
        val maxLen = maxOf(s1.length, s2.length)
        return 1.0f - (dist.toFloat() / maxLen.toFloat())
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
