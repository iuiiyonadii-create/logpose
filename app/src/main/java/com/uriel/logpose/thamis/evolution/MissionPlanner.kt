package com.uriel.logpose.thamis.evolution

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.evolution.model.Anomaly
import com.uriel.logpose.thamis.evolution.model.AnomalyType
import com.uriel.logpose.thamis.evolution.model.Mission
import com.uriel.logpose.thamis.evolution.model.Priority

/**
 * MissionPlanner: El estratega de THAMIS.
 * Convierte anomalías detectadas en planes de acción (Misiones).
 */
object MissionPlanner {

    private val missionHistory = mutableListOf<Mission>()

    /**
     * Procesa una lista de anomalías y genera misiones priorizadas.
     */
    fun planMissions(anomalies: List<Anomaly>): List<Mission> {
        val newMissions = anomalies.map { anomaly ->
            createMissionFor(anomaly)
        }.sortedBy { it.priority }

        missionHistory.addAll(newMissions)
        return newMissions
    }

    private fun createMissionFor(anomaly: Anomaly): Mission {
        val priority = when {
            anomaly.impact > 0.8f -> Priority.CRITICAL
            anomaly.impact > 0.6f -> Priority.ALTO
            anomaly.impact > 0.4f -> Priority.MEDIO
            else -> Priority.BAJO
        }

        val title = when (anomaly.type) {
            AnomalyType.BLUETOOTH_INSTABILITY -> "Misión #007: Bluetooth Field Intelligence"
            else -> "Optimización: ${anomaly.type}"
        }
        
        val goal = anomaly.description
        
        // Identificar archivos potencialmente afectados basado en el tipo de anomalía
        val files = when (anomaly.type) {
            AnomalyType.LATENCY_SPIKE -> listOf("WorldModelEngine.kt", "WorldAudit.kt")
            AnomalyType.RECOGNITION_FAILURE -> listOf("IntentDetector.kt", "AdvancedLanguageEngine.kt")
            AnomalyType.BLUETOOTH_INSTABILITY -> listOf("BluetoothCommunicationManager.kt", "VoiceManager.kt")
            else -> emptyList()
        }

        LogPoseLogger.i("MissionPlanner: Creada misión '$title' con prioridad $priority.")

        return Mission(
            title = title,
            goal = goal,
            priority = priority,
            anomalySourceId = anomaly.id,
            filesAffected = files,
            testMethods = listOf("validateBluetoothStability", "measureScoHandshake")
        )
    }

    fun getActiveMissions() = missionHistory.filter { it.status != com.uriel.logpose.thamis.evolution.model.MissionStatus.COMPLETED }
}
