package com.uriel.logpose.thamis.lab.simulation

import com.uriel.logpose.core.Command
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.context.ContextEngine
import com.uriel.logpose.thamis.context.ContextSnapshot
import com.uriel.logpose.thamis.context.ConversationState
import com.uriel.logpose.thamis.context.FocusDomain
import com.uriel.logpose.thamis.orchestrator.SystemOrchestrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Simulador de Escenarios para THAMIS LAB.
 * Permite probar el cerebro sin estar en la moto.
 */
object ThamisLabSimulator {

    private val scope = CoroutineScope(Dispatchers.Main)

    /**
     * Simula un viaje a alta velocidad con un comando de riesgo.
     */
    fun simulateHighSpeedRisk(customSpeed: Float = 145f) {
        scope.launch {
            LogPoseLogger.i("SIMULATOR: Iniciando escenario de ALTA VELOCIDAD ($customSpeed km/h)...")
            
            // 1. Inyectar velocidad
            injectState(speed = customSpeed, battery = 80)
            delay(1000)

            // 2. Intentar comando prohibido
            LogPoseLogger.i("SIMULATOR: Intentando llamar a un contacto a $customSpeed km/h...")
            SystemOrchestrator.dispatchCommand(Command.CALL_CONTACT)
        }
    }

    /**
     * Simula batería baja y sugerencia proactiva.
     */
    fun simulateLowBattery(customBattery: Int = 12) {
        scope.launch {
            LogPoseLogger.i("SIMULATOR: Iniciando escenario de BATERÍA BAJA ($customBattery%)...")
            injectState(speed = 40f, battery = customBattery)
        }
    }

    /**
     * Simula un comando de música exitoso.
     */
    fun simulateMusicFlow() {
        scope.launch {
            LogPoseLogger.i("SIMULATOR: Iniciando escenario de MÚSICA...")
            injectState(speed = 50f, battery = 90)
            delay(1000)
            SystemOrchestrator.dispatchCommand(Command.PLAY_MUSIC)
        }
    }

    /**
     * Prueba el Cerebro Remoto (PC/Nube) con un comando complejo.
     */
    fun simulateRemoteAiQuery() {
        scope.launch {
            LogPoseLogger.i("SIMULATOR: Probando consulta a IA Remota...")
            val complexText = "Me gustaría escuchar algo de rock nacional argentino de los 80"
            val request = com.uriel.logpose.thamis.request.THAMISRequest(text = complexText)
            
            // Forzamos el uso de ThamisBrain para ver si llega a la PC
            val decision = com.uriel.logpose.thamis.intelligence.ThamisBrain.process(request)
            
            LogPoseLogger.i("SIMULATOR: IA Remota respondió: ${decision.intent} (Conf: ${decision.confidence})")
            
            if (decision.intent != com.uriel.logpose.thamis.intent.Intent.UNKNOWN) {
                com.uriel.logpose.features.voice.FeedbackManager.speak("La IA de tu PC dice que quieres: ${decision.intent}")
            } else {
                com.uriel.logpose.features.voice.FeedbackManager.speak("No pude conectar con el cerebro de tu PC.")
            }
        }
    }

    /**
     * Simula degradación del sistema para probar Auto-Fixing.
     */
    fun simulateSystemDegradation() {
        scope.launch {
            LogPoseLogger.w("SIMULATOR: Degradando salud del sistema...")
            SystemOrchestrator.updateHealth(40)
            delay(2000)
            LogPoseLogger.i("SIMULATOR: El QualityAgent debería detectar la degradación.")
        }
    }

    private fun injectState(speed: Float, battery: Int) {
        val snapshot = ContextSnapshot(
            speedKmh = speed,
            gpsActive = true,
            isPlayingMusic = false,
            isPhoneCallActive = false,
            bluetoothConnected = true,
            currentFocus = FocusDomain.NONE,
            conversationState = ConversationState.IDLE,
            batteryLevel = battery,
            noiseLevelDb = 40f,
            lastIntent = null,
            activeDomain = null
        )
        ContextEngine.takeSnapshot(snapshot)
    }
}
