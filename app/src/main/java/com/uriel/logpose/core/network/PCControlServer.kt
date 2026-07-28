package com.uriel.logpose.core.network

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.lab.simulation.ThamisLabSimulator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket

/**
 * Servidor de escucha para control remoto desde la PC hacia THAMIS LAB.
 */
object PCControlServer {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var socket: DatagramSocket? = null
    private var isRunning = false

    fun start() {
        if (isRunning) return
        isRunning = true
        
        scope.launch {
            try {
                socket = DatagramSocket(5051)
                val buffer = ByteArray(1024)
                LogPoseLogger.i("PCControlServer: Escuchando en puerto 5051...")

                while (isRunning) {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket?.receive(packet)
                    val message = String(packet.data, 0, packet.length)
                    handleRemoteCommand(message)
                }
            } catch (e: Exception) {
                LogPoseLogger.e("PCControlServer: Error en servidor: ${e.message}")
            }
        }
    }

    private fun handleRemoteCommand(cmd: String) {
        LogPoseLogger.i("PCControlServer: Comando remoto recibido -> $cmd")
        
        val parts = cmd.split(":")
        val mainCmd = parts.getOrNull(1) ?: ""
        val param = parts.getOrNull(2)

        when (mainCmd) {
            "SIM_DEGRADATION" -> ThamisLabSimulator.simulateSystemDegradation()
            "SIM_MUSIC" -> ThamisLabSimulator.simulateMusicFlow()
            "SIM_SPEED" -> {
                val speed = param?.toFloatOrNull() ?: 145f
                ThamisLabSimulator.simulateHighSpeedRisk(speed)
            }
            "SIM_BATTERY" -> {
                val battery = param?.toIntOrNull() ?: 12
                ThamisLabSimulator.simulateLowBattery(battery)
            }
            "AI_REPORT" -> {
                com.uriel.logpose.features.voice.FeedbackManager.speak("Generando reporte de inteligencia para la PC.")
                com.uriel.logpose.thamis.multiagent.SelfImprovementEngine.performSelfAudit()
            }
            "SET_RIDER_PROFILE" -> {
                val profile = param ?: "Genérico"
                com.uriel.logpose.features.voice.FeedbackManager.speak("Perfil de hardware configurado: $profile")
            }
            "INJECT_MSG" -> {
                val msg = param ?: ""
                com.uriel.logpose.features.voice.FeedbackManager.speak("Mensaje del cliente: $msg")
            }
            else -> LogPoseLogger.w("PCControlServer: Comando no reconocido: $cmd")
        }
    }

    fun stop() {
        isRunning = false
        socket?.close()
    }
}
