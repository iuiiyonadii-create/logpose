package com.uriel.logpose.thamis.evolution

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.evolution.model.Anomaly
import com.uriel.logpose.thamis.evolution.model.AnomalyType
import java.util.*

/**
 * ResourceIntelligenceEngine: Monitorea el consumo de RAM, CPU y estado de batería.
 * Implementa la Fase 1 y 2 de la Misión #009.
 */
object ResourceIntelligenceEngine {

    data class ResourceSnapshot(
        val totalMemoryMb: Long,
        val freeMemoryMb: Long,
        val appMemoryMb: Long,
        val activeThreads: Int,
        val timestamp: Long = System.currentTimeMillis()
    )

    fun captureResourceSnapshot(context: Context): ResourceSnapshot {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val runtime = Runtime.getRuntime()
        val appMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        val totalMem = memoryInfo.totalMem / (1024 * 1024)
        val freeMem = memoryInfo.availMem / (1024 * 1024)
        val threads = Thread.activeCount()

        val snapshot = ResourceSnapshot(totalMem, freeMem, appMemory, threads)
        LogPoseLogger.d("ResourceIntel: RAM App: ${appMemory}MB | RAM Free: ${freeMem}MB | Threads: $threads")
        
        return snapshot
    }

    /**
     * Analiza el consumo y busca anomalías (Fase 2).
     */
    fun detectResourceAnomalies(context: Context): List<Anomaly> {
        val snapshot = captureResourceSnapshot(context)
        val anomalies = mutableListOf<Anomaly>()

        // 1. Umbral de RAM para el S8 (4GB total, usualmente ~1.5GB libre)
        if (snapshot.appMemoryMb > 250) {
            anomalies.add(Anomaly(
                type = AnomalyType.BATTERY_DRAIN, // Usamos battery drain como proxy de alto consumo general
                impact = 0.6f,
                frequency = 1,
                description = "Alto consumo de RAM detectado (${snapshot.appMemoryMb}MB). Posible retención de modelos pesados.",
                modules = listOf("Vosk", "NLU")
            ))
        }

        // 2. Umbral de hilos (Threads)
        if (snapshot.activeThreads > 100) {
            anomalies.add(Anomaly(
                type = AnomalyType.SYSTEM_CRASH,
                impact = 0.5f,
                frequency = 1,
                description = "Exceso de hilos activos (${snapshot.activeThreads}). Posible fuga de coroutines o threads.",
                modules = listOf("System", "Bluetooth")
            ))
        }

        return anomalies
    }
}
