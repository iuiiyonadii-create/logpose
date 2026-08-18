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
        // v73.1: Refactor de Split Seguro (Misión Anti-Glitches)
        // Usamos split con límite para no romper los parámetros JSON
        val strippedCmd = cmd.trim()
        val parts = strippedCmd.split(":", limit = 3)
        
        val mainCmd = parts.getOrNull(1) ?: ""
        val param = parts.getOrNull(2)

        LogPoseLogger.i("PCControlServer", "Authorized command: $mainCmd | Param: ${param?.take(20)}...")

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
            "HOT_PATCH" -> {
                val patch = param ?: ""
                com.uriel.logpose.thamis.learning.LearningEngine.applyHotPatch(patch)
            }
            "REMOTE_SPEAK" -> {
                val text = param ?: ""
                com.uriel.logpose.features.voice.FeedbackManager.speak(text)
            }
            "SENSITIVITY" -> {
                val level = param?.toFloatOrNull() ?: 0.5f
                com.uriel.logpose.core.app.LogPoseApplication.entryPoint.voskVoiceEngine().setSensitivity(level)
            }
            "SYSTEM_ACTION" -> {
                val action = param ?: ""
                handleSystemAction(action)
            }
            else -> LogPoseLogger.w("PCControlServer: Unknown command: $strippedCmd")
        }
    }

    private fun handleSystemAction(action: String) {
        val parts = action.split(":")
        val type = parts.getOrNull(0) ?: ""
        val value = parts.getOrNull(1) ?: ""

        when (type) {
            "OPEN_APP" -> {
                LogPoseLogger.i("PCControlServer", "Remote App Launch: $value")
                val launcher = com.uriel.logpose.core.app.AppLauncherImpl(com.uriel.logpose.core.app.LogPoseApplication.instance)
                launcher.openApp(value)
            }
            "NAVIGATE" -> {
                com.uriel.logpose.features.navigation.engine.GoogleMapsDispatcher.lanzarNavegacion(com.uriel.logpose.core.app.LogPoseApplication.instance, value)
            }
        }
    }

    fun stop() {
        isRunning = false
        socket?.close()
        socket = null
    }
}
