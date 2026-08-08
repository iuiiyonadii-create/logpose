package com.thamis.lab.simulation.environment

import com.thamis.lab.core.common.telemetry.LabTelemetry
import kotlin.random.Random

/**
 * DynamicEnvironmentSimulator: Genera escenarios acústicos variables que imitan un día real.
 */
public class DynamicEnvironmentSimulator {

    public enum class EnvironmentType {
        CITY_TRAFFIC,
        HIGHWAY_STORM,
        TUNNEL_ECHO,
        WINDY_COAST,
        QUIET_GARAGE
    }

    public data class AcousticCondition(
        val type: EnvironmentType,
        val noiseLevel: Float, // 0.0 to 1.0
        val interferenceFrequency: Int, // Hz
        val description: String
    )

    public fun generateRandomCondition(): AcousticCondition {
        val type = EnvironmentType.values().random()
        val condition = when (type) {
            EnvironmentType.CITY_TRAFFIC -> AcousticCondition(type, 0.4f, 400, "Constant urban hum with occasional horns")
            EnvironmentType.HIGHWAY_STORM -> AcousticCondition(type, 0.9f, 2000, "High velocity wind and rain impact")
            EnvironmentType.TUNNEL_ECHO -> AcousticCondition(type, 0.6f, 800, "Reverberation and low frequency tire noise")
            EnvironmentType.WINDY_COAST -> AcousticCondition(type, 0.75f, 1500, "Strong lateral wind gusts")
            EnvironmentType.QUIET_GARAGE -> AcousticCondition(type, 0.1f, 50, "Minimal ambient noise")
        }
        
        LabTelemetry.logEvent("EnvSim", "Dynamic Environment Shift: ${condition.type} (Noise: ${condition.noiseLevel})")
        return condition
    }
}
