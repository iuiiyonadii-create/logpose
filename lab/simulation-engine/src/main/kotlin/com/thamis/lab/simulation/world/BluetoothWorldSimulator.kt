package com.thamis.lab.simulation.world

import com.thamis.lab.core.common.logging.LabLogger

public data class VirtualBluetoothEnvironment(
    public val activeIntercomModel: String,
    public val signalQualityDbm: Int,
    public val packetLossRatio: Double,
    public val activeProfile: String
)

public data class GpsRouteSimulation(
    public val routeName: String,
    public val currentLatitude: Double,
    public val currentLongitude: Double,
    public val speedKmh: Double,
    public val isTunnel: Boolean
)

public data class NetworkLabState(
    public val networkType: String, // 2G, 3G, 4G, 5G, WiFi, Offline
    public val latencyMs: Long,
    public val packetLossPercent: Double,
    public val bandwidthKbps: Long
)

public data class SimulatedNotification(
    public val appName: String,
    public val sender: String,
    public val messageText: String,
    public val priority: String
)

/**
 * World Environment Simulators covering Virtual Bluetooth, GPX Routes, Network Profiles, and Notification Streams.
 */
public class BluetoothWorldSimulator {
    public fun simulateEnvironment(model: String = "Cardo Packtalk Edge"): VirtualBluetoothEnvironment {
        LabLogger.info("BluetoothWorldSimulator", "Simulating Bluetooth world for $model...")
        return VirtualBluetoothEnvironment(
            activeIntercomModel = model,
            signalQualityDbm = -62,
            packetLossRatio = 0.001,
            activeProfile = "A2DP"
        )
    }
}

public class AdvancedGpsEngine {
    public fun simulateRoute(routeName: String = "Highway-Route-01"): GpsRouteSimulation {
        LabLogger.info("AdvancedGpsEngine", "Simulating GPX route '$routeName'...")
        return GpsRouteSimulation(
            routeName = routeName,
            currentLatitude = -34.6037,
            currentLongitude = -58.3816,
            speedKmh = 95.0,
            isTunnel = false
        )
    }
}

public class NetworkLabSimulator {
    public fun simulateNetwork(type: String = "4G"): NetworkLabState {
        LabLogger.info("NetworkLabSimulator", "Simulating network state '$type'...")
        return NetworkLabState(
            networkType = type,
            latencyMs = 35L,
            packetLossPercent = 0.0,
            bandwidthKbps = 25000L
        )
    }
}

public class NotificationLabSimulator {
    public fun generateNotificationStream(): List<SimulatedNotification> {
        LabLogger.info("NotificationLabSimulator", "Generating notification stream...")
        return listOf(
            SimulatedNotification("WhatsApp", "Soporte", "Mensaje recibido", "HIGH"),
            SimulatedNotification("Spotify", "Player", "Reproduciendo lista", "LOW")
        )
    }
}
