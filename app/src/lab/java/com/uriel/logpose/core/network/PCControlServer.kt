package com.uriel.logpose.core.network

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.lab.simulation.ThamisLabSimulator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket

/**
 * Server for listening to remote commands from PC (Lab only).
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
                socket = DatagramSocket(NetworkConfig.PC_CONTROL_PORT)
                val buffer = ByteArray(1024)
                LogPoseLogger.i("PCControlServer: Listening on port ${NetworkConfig.PC_CONTROL_PORT}...")

                while (isRunning) {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket?.receive(packet)
                    val message = String(packet.data, 0, packet.length)
                    handleRemoteCommand(message)
                }
            } catch (e: Exception) {
                LogPoseLogger.e("PCControlServer: Server error: ${e.message}")
            }
        }
    }

    private fun handleRemoteCommand(cmd: String) {
        // Security check: Only accept commands starting with the API Key
        if (!cmd.startsWith(NetworkConfig.THAMIS_API_KEY)) {
            LogPoseLogger.w("PCControlServer: Unauthorized command attempt.")
            return
        }

        val strippedCmd = cmd.removePrefix("${NetworkConfig.THAMIS_API_KEY}:")
        LogPoseLogger.i("PCControlServer: Authorized remote command -> $strippedCmd")
        
        val parts = strippedCmd.split(":")
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
                com.uriel.logpose.features.voice.FeedbackManager.speak("Generating intelligence report for PC.")
                com.uriel.logpose.thamis.multiagent.SelfImprovementEngine.performSelfAudit()
            }
            "SET_RIDER_PROFILE" -> {
                val profile = param ?: "Generic"
                com.uriel.logpose.features.voice.FeedbackManager.speak("Hardware profile set: $profile")
            }
            "INJECT_MSG" -> {
                val msg = param ?: ""
                com.uriel.logpose.features.voice.FeedbackManager.speak("Client message: $msg")
            }
            else -> LogPoseLogger.w("PCControlServer: Unknown command: $strippedCmd")
        }
    }

    fun stop() {
        isRunning = false
        socket?.close()
        socket = null
    }
}
