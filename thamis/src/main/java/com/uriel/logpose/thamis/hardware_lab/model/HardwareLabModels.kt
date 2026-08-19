package com.uriel.logpose.thamis.hardware_lab.model

/**
 * Perfil técnico detallado de un dispositivo físico bajo prueba.
 */
data class PhysicalDeviceProfile(
    val model: String,
    val manufacturer: String,
    val androidVersion: Int,
    val ramGb: Int,
    val bluetoothVersion: String,
    val microphoneQuality: String // HIGH, MEDIUM, LOW
)

/**
 * Resultado de un benchmark de hardware.
 */
data class HardwareBenchmarkResult(
    val deviceId: String,
    val operation: String,
    val averageMs: Long,
    val tempDeltaCelsius: Float,
    val batteryDrainMa: Float
)

/**
 * Reporte de validación final para un dispositivo específico.
 */
data class HardwareValidationReport(
    val deviceProfile: PhysicalDeviceProfile,
    val status: String, // COMPATIBLE, WARNING, INCOMPATIBLE
    val findings: List<String>,
    val performanceScore: Float // 0.0 to 1.0
)
