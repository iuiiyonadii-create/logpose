package com.uriel.logpose.thamis.security.monitor

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.security.model.ResourceType

/**
 * Supervisa el acceso a sensores físicos para detectar usos inesperados.
 */
object SensorAccessMonitor {

    fun monitorSensor(sensor: ResourceType, durationMs: Long) {
        if (durationMs > 30000 && sensor == ResourceType.MICROPHONE) {
            LogPoseLogger.w("THAMIS_SENSOR: Uso prolongado del micrófono detectado (${durationMs}ms)")
        }
    }

    fun validateAccess(module: String, sensor: ResourceType): Boolean {
        // En v1.0, validamos que solo ciertos módulos accedan a ciertos sensores
        return when (sensor) {
            ResourceType.MICROPHONE -> module == "VoiceManager" || module == "SpeechRecognition"
            ResourceType.LOCATION -> module == "NavigationProvider" || module == "WorldModel"
            ResourceType.BLUETOOTH -> module == "BluetoothRepository" || module == "IntercomManager"
            else -> true
        }
    }
}
