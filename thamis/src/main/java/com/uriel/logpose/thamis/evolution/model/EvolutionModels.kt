package com.uriel.logpose.thamis.evolution.model

import java.util.*

enum class AnomalyType {
    LATENCY_SPIKE,
    RECOGNITION_FAILURE,
    BLUETOOTH_INSTABILITY,
    BATTERY_DRAIN,
    USER_CORRECTION,
    SYSTEM_CRASH,
    ACTUATION_ERROR
}

enum class Priority {
    CRITICAL, ALTO, MEDIO, BAJO
}

data class Anomaly(
    val id: String = UUID.randomUUID().toString(),
    val type: AnomalyType,
    val impact: Float, // 0.0 to 1.0
    val frequency: Int,
    val description: String,
    val modules: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)

data class Mission(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val goal: String,
    val priority: Priority,
    val anomalySourceId: String,
    val filesAffected: List<String>,
    val testMethods: List<String>,
    val status: MissionStatus = MissionStatus.PLANNED
)

enum class MissionStatus {
    PLANNED, INVESTIGATING, HYPOTHESIS_READY, IN_SANDBOX, VALIDATING, COMPLETED, FAILED
}
