package com.uriel.logpose.core.parser

import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.compat.core.LogPoseLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.atomic.AtomicBoolean

/**
 * LabDiscoveryService: Autonomously detects the PC Bridge via UDP Broadcast.
 * Eliminates the need for static IP configuration.
 * v2.0: Reactive Flow integration and Heartbeat tracking.
 */
object LabDiscoveryService {

    private const val DISCOVERY_PORT = 5051
    private const val MAGIC_TOKEN = "LOGPOSE_BRIDGE_HERE"
    private const val HEARTBEAT_TIMEOUT_MS = 15000L

    private val _pcIp = MutableStateFlow<String?>(null)
    val pcIp: StateFlow<String?> = _pcIp.asStateFlow()

    private val isRunning = AtomicBoolean(false)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var discoveryJob: Job? = null
    private var watchdogJob: Job? = null
    private var lastSeenTimestamp = 0L

    fun start() {
        if (isRunning.getAndSet(true)) return
        
        LogPoseLogger.i("📡 Discovery: Lab Discovery Service v2.0 Active.")
        
        // Start with the last known IP from settings
        val storedIp = LogPoseApplication.entryPoint.settingsManager().getString("pc_ip", null)
        _pcIp.value = storedIp

        discoveryJob = scope.launch {
            while (isActive) {
                try {
                    listenForBridge()
                } catch (e: Exception) {
                    if (isActive) {
                        LogPoseLogger.e("📡 Discovery: UDP Listener failed: ${e.message}. Restarting in 5s...")
                        delay(5000)
                    }
                }
            }
        }

        startWatchdog()
    }

    private suspend fun listenForBridge() = withContext(Dispatchers.IO) {
        try {
            DatagramSocket(DISCOVERY_PORT).use { socket ->
                socket.broadcast = true
                socket.soTimeout = 5000 
                val buffer = ByteArray(1024)
                
                LogPoseLogger.i("📡 Discovery: Buscando laboratorio en puerto $DISCOVERY_PORT...")
                
                while (isActive) {
                    try {
                        val packet = DatagramPacket(buffer, buffer.size)
                        socket.receive(packet)
                        
                        val message = String(packet.data, 0, packet.length).trim()
                        if (message.startsWith(MAGIC_TOKEN)) {
                            val ip = packet.address.hostAddress ?: continue
                            LogPoseLogger.i("📡 Discovery: ¡PAQUETE RECIBIDO! Bridge detectado en: $ip")
                            handleBridgeDetection(ip)
                        }
                    } catch (e: java.net.SocketTimeoutException) {
                        // LogPoseLogger.d("📡 Discovery: Reintentando escucha...")
                    }
                }
            }
        } catch (e: Exception) {
            LogPoseLogger.e("📡 Discovery: Error al abrir socket (¿Puerto en uso?): ${e.message}")
            delay(5000)
        }
    }

    private fun handleBridgeDetection(ip: String) {
        lastSeenTimestamp = System.currentTimeMillis()
        if (_pcIp.value != ip) {
            LogPoseLogger.i("📡 Discovery: NEW Bridge detected at $ip")
            _pcIp.value = ip
            
            // Persist in settings
            LogPoseApplication.entryPoint.settingsManager().setString("pc_ip", ip)
        }
    }

    private fun startWatchdog() {
        watchdogJob = scope.launch {
            while (isActive) {
                delay(5000)
                if (_pcIp.value != null && System.currentTimeMillis() - lastSeenTimestamp > HEARTBEAT_TIMEOUT_MS) {
                    LogPoseLogger.w("📡 Discovery: Bridge connection lost (Heartbeat timeout).")
                    // We keep the IP but could optionally null it if we want to force re-discovery
                    // _pcIp.value = null 
                }
            }
        }
    }

    fun stop() {
        isRunning.set(false)
        discoveryJob?.cancel()
        watchdogJob?.cancel()
        _pcIp.value = null
    }

    fun isBridgeOnline(): Boolean = _pcIp.value != null && (System.currentTimeMillis() - lastSeenTimestamp < HEARTBEAT_TIMEOUT_MS)
}
