package com.uriel.logpose.thamis.evolution

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.evolution.model.Anomaly
import com.uriel.logpose.thamis.evolution.model.AnomalyType
import com.uriel.logpose.thamis.world.audit.WorldAudit
import com.uriel.logpose.thamis.world.engine.WorldModelEngine
import com.uriel.logpose.core.app.LogPoseApplication

/**
 * DiscoveryEngine: El ojo vigía de THAMIS. 
 * Escanea métricas y logs en busca de ineficiencias o errores.
 */
object DiscoveryEngine {

    private val detectedAnomalies = mutableListOf<Anomaly>()

    /**
     * Ejecuta un escaneo completo del sistema.
     */
    fun performDiscoveryScan(): List<Anomaly> {
        LogPoseLogger.i("DiscoveryEngine: Iniciando escaneo de anomalías...")
        
        val anomalies = mutableListOf<Anomaly>()

        // 1. Analizar Latencia en WorldAudit
        anomalies.addAll(scanLatency())

        // 2. Analizar Errores de Reconocimiento / Correcciones
        anomalies.addAll(scanUserCorrections())

        // 3. Analizar Estado del Hardware (Bluetooth/Batería)
        anomalies.addAll(scanHardwareStability())

        // 4. Analizar Inteligencia Bluetooth (Fase 007)
        anomalies.addAll(scanBluetoothFieldIntelligence())
        
        // 5. Analizar Recursos (Fase 009)
        anomalies.addAll(scanResources())

        detectedAnomalies.addAll(anomalies)
        LogPoseLogger.i("DiscoveryEngine: Escaneo finalizado. Se detectaron ${anomalies.size} anomalías.")
        
        return anomalies
    }

    private fun scanLatency(): List<Anomaly> {
        val traces = WorldAudit.getLogs()
        val highLatencyTraces = traces.filter { it.latencyMs > 300 }
        
        if (highLatencyTraces.isNotEmpty()) {
            return listOf(Anomaly(
                type = AnomalyType.LATENCY_SPIKE,
                impact = 0.6f,
                frequency = highLatencyTraces.size,
                description = "Detectado aumento de latencia en WorldModel (>300ms) en ${highLatencyTraces.size} eventos.",
                modules = highLatencyTraces.map { it.affectedDomain }.distinct()
            ))
        }
        return emptyList()
    }

    private fun scanUserCorrections(): List<Anomaly> {
        return emptyList()
    }

    private fun scanHardwareStability(): List<Anomaly> {
        val snapshot = WorldModelEngine.getCurrentSnapshot()
        
        if (snapshot.vehicle.moving && snapshot.systems.navigation.gpsStatus != com.uriel.logpose.thamis.world.model.GpsStatus.READY) {
            return listOf(Anomaly(
                type = AnomalyType.ACTUATION_ERROR,
                impact = 0.8f,
                frequency = 1,
                description = "Vehículo en movimiento sin señal de GPS estable.",
                modules = listOf("Navigation")
            ))
        }
        
        return emptyList()
    }

    private fun scanBluetoothFieldIntelligence(): List<Anomaly> {
        val report = BluetoothIntelligence.analyzeStability()
        val anomalies = mutableListOf<Anomaly>()

        if (report.disconnectCount > 0) {
            anomalies.add(Anomaly(
                type = AnomalyType.BLUETOOTH_INSTABILITY,
                impact = 0.7f,
                frequency = report.disconnectCount,
                description = "Detectadas ${report.disconnectCount} desconexiones Bluetooth en la sesión actual.",
                modules = listOf("Bluetooth", "Audio")
            ))
        }

        if (report.avgScoHandshakeMs > 1500) {
            val impact = if (report.avgScoHandshakeMs > 2000) 0.7f else 0.5f
            anomalies.add(Anomaly(
                type = AnomalyType.BLUETOOTH_INSTABILITY,
                impact = impact,
                frequency = 1,
                description = "Latencia de Handshake SCO elevada (${report.avgScoHandshakeMs}ms). El intercomunicador podría estar experimentando lag de firmware.",
                modules = listOf("Bluetooth", "Voice")
            ))
        }

        return anomalies
    }

    private fun scanResources(): List<Anomaly> {
        return ResourceIntelligenceEngine.detectResourceAnomalies(LogPoseApplication.instance)
    }

    fun getDetectedAnomalies() = detectedAnomalies.toList()
}
