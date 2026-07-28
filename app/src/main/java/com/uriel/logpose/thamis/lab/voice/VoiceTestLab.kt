package com.uriel.logpose.thamis.lab.voice

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.lab.model.VoiceTestEntry

/**
 * Laboratorio para probar el reconocimiento de voz y normalización fonética.
 */
object VoiceTestLab {
    private val testLogs = mutableListOf<VoiceTestEntry>()

    fun runTest(original: String, recognized: String, context: String = "IDLE") {
        val entry = VoiceTestEntry(
            originalPhrase = original,
            recognizedPhrase = recognized,
            correction = if (original != recognized) original else null,
            context = context,
            conditions = "Simulated Noise"
        )
        testLogs.add(entry)
        
        LogPoseLogger.i("THAMIS_VOICE_TEST: Input: '$recognized' | Expected: '$original'")
        
        if (original != recognized) {
            LogPoseLogger.w("THAMIS_VOICE_TEST: Discrepancia detectada. Generando reporte de mejora fonética.")
        }
    }

    fun getLogs(): List<VoiceTestEntry> = testLogs.toList()
}
