package com.uriel.logpose.core.network

import com.uriel.logpose.core.compat.core.LogPoseLogger
import kotlinx.coroutines.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import javax.inject.Inject

/**
 * Lab implementation of PCBridge.
 */
class PCBridgeLabImpl @Inject constructor() : PCBridge {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var sequenceNumber = 0L // Misión #060: Anti-Replay ID
    private var heartbeatJob: Job? = null

    init {
        startHeartbeat()
    }

    private fun startHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = scope.launch {
            while (isActive) {
                sendHeartbeat()
                delay(30000) // v69.6: Aumentado a 30s para eliminar interferencia Wi-Fi
            }
        }
    }

    private fun sendHeartbeat() {
        val ip = com.uriel.logpose.core.parser.LabDiscoveryService.pcIp.value ?: return
        val port = 5055
        
        try {
            DatagramSocket().use { socket ->
                val address = InetAddress.getByName(ip)
                val rawPayload = "--- Latido de Sincronización ---"
                val encryptedPayload = NetworkConfig.encryptPayload(rawPayload)
                
                val currentSeq = sequenceNumber++
                val signature = NetworkConfig.generateStaffSignature("$encryptedPayload:$currentSeq")
                
                val trace = org.json.JSONObject().apply {
                    put("type", "HEARTBEAT")
                    put("payload_enc", encryptedPayload)
                    put("staff_sig", signature)
                    put("seq", currentSeq)
                }
                val message = trace.toString().toByteArray()
                val packet = DatagramPacket(message, message.size, address, port)
                socket.send(packet)
            }
        } catch (e: Exception) { /* Silently fail heartbeat */ }
    }

    override fun sendCommand(action: String) {
        val ip = com.uriel.logpose.core.parser.LabDiscoveryService.pcIp.value ?: return
        val port = 5055

        scope.launch {
            try {
                DatagramSocket().use { socket ->
                    val address = InetAddress.getByName(ip)
                    val rawPayload = "HUD_ACTION:$action"
                    val encryptedPayload = NetworkConfig.encryptPayload(rawPayload)
                    
                    val currentSeq = sequenceNumber++
                    val signature = NetworkConfig.generateStaffSignature("$encryptedPayload:$currentSeq")
                    
                    val trace = org.json.JSONObject().apply {
                        put("type", "COGNITIVE_PIPELINE_TRACE")
                        put("payload_enc", encryptedPayload)
                        put("detected_intent", "UI_EVENT")
                        put("status", "🟢 OK")
                        put("staff_sig", signature)
                        put("seq", currentSeq)
                    }
                    val message = trace.toString().toByteArray()
                    val packet = DatagramPacket(message, message.size, address, port)
                    socket.send(packet)
                    LogPoseLogger.i("PCBridgeLabImpl: Force-Sent UI event to $ip:$port")
                }
            } catch (e: Exception) {
                LogPoseLogger.e("PCBridgeLabImpl: Error: ${e.message}")
            }
        }
    }

    override fun startRemoteServer() {
        PCControlServer.start()
    }

    override fun stopRemoteServer() {
        PCControlServer.stop()
    }

    override fun setHeartbeatEnabled(enabled: Boolean) {
        if (enabled) {
            startHeartbeat()
        } else {
            heartbeatJob?.cancel()
            heartbeatJob = null
            LogPoseLogger.i("PCBridge: Latidos suspendidos por ahorro energético.")
        }
    }
}
