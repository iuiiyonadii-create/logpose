package com.uriel.logpose.core.network

import com.uriel.logpose.core.app.AppContainer
import com.uriel.logpose.core.compat.core.LogPoseLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * Bridge para control remoto de PC.
 * Corregido: Ahora usa sockets efímeros para evitar fugas de recursos.
 */
object PCBridge {

    private val scope = CoroutineScope(Dispatchers.IO)

    fun sendCommand(action: String) {
        val ip = AppContainer.settingsManager.getString("pc_ip", "192.168.1.33") ?: "192.168.1.33"
        val port = 9999

        scope.launch {
            try {
                // Usamos 'use' para asegurar que el socket se cierre siempre
                DatagramSocket().use { socket ->
                    val address = InetAddress.getByName(ip)
                    val message = action.toByteArray()
                    val packet = DatagramPacket(message, message.size, address, port)
                    socket.send(packet)
                    LogPoseLogger.i("PCBridge: Enviado '$action' a $ip:$port")
                }
            } catch (e: Exception) {
                LogPoseLogger.e("PCBridge: Error enviando comando: ${e.message}")
            }
        }
    }
}
