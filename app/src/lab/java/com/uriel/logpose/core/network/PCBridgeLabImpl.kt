package com.uriel.logpose.core.network

import com.uriel.logpose.core.compat.core.LogPoseLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import javax.inject.Inject

/**
 * Lab implementation of PCBridge.
 */
class PCBridgeLabImpl @Inject constructor() : PCBridge {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun sendCommand(action: String) {
        val ip = NetworkConfig.getPCIp()
        val port = NetworkConfig.PC_PORT

        scope.launch {
            try {
                DatagramSocket().use { socket ->
                    val address = InetAddress.getByName(ip)
                    // Security: Include API Key in payload for UDP control
                    val securedAction = "${NetworkConfig.THAMIS_API_KEY}:$action"
                    val message = securedAction.toByteArray()
                    val packet = DatagramPacket(message, message.size, address, port)
                    socket.send(packet)
                    LogPoseLogger.i("PCBridgeLabImpl: Sent secured command to $ip:$port")
                }
            } catch (e: Exception) {
                LogPoseLogger.e("PCBridgeLabImpl: Error sending command: ${e.message}")
            }
        }
    }

    override fun startRemoteServer() {
        PCControlServer.start()
    }

    override fun stopRemoteServer() {
        PCControlServer.stop()
    }
}
