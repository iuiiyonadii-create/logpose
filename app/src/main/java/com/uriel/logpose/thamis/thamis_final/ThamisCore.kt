package com.uriel.logpose.thamis.thamis_final

import android.content.Context
import com.uriel.logpose.core.compat.core.AppState
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.thamis.Event
import com.uriel.logpose.core.thamis.EventBus
import com.uriel.logpose.core.thamis.EventType
import com.uriel.logpose.domain.models.LogPoseDevice
import com.uriel.logpose.thamis_ai.decision.DecisionEngine
import com.uriel.logpose.thamis_ai.learning.LearningEngine
import com.uriel.logpose.thamis_ai.memory.MemoryManager
import com.uriel.logpose.thamis_ai.nlu.NaturalLanguageEngine
import com.uriel.logpose.thamis_ai.voice.VoiceInteractionManager
import com.uriel.logpose.thamis_ai.safety.SafetyEngine
import com.uriel.logpose.thamis.context.ContextEngine
import com.uriel.logpose.thamis.autonomy.AutonomyEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * THAMIS CORE FINAL — El cerebro unificado de LogPose.
 * Integra todos los motores de inteligencia, seguridad y contexto.
 */
class ThamisCore(private val context: Context) {

    private val eventBus = EventBus()
    private val memory = MemoryManager()
    private val learning = LearningEngine()
    private val nlu = NaturalLanguageEngine()
    private val decision = DecisionEngine()
    private val safety = SafetyEngine()
    private val voice = VoiceInteractionManager(context)

    private val _state = MutableStateFlow(AppState.STOPPED)
    val state: StateFlow<AppState> = _state.asStateFlow()

    private var currentDevice: LogPoseDevice? = null

    fun initialize() {
        LogPoseLogger.i("ThamisCore: Iniciando Cerebro Central...")
        
        // Inicialización de componentes de LABS
        ContextEngine.getActiveContext()
        AutonomyEngine.getLevel()
        
        LogPoseLogger.i("ThamisCore: Arquitectura unificada activa.")
    }

    fun onBluetoothConnected(device: LogPoseDevice) {
        currentDevice = device
        LogPoseLogger.i("ThamisCore: Bluetooth conectado: ${device.name}")
        _state.value = AppState.READY
    }

    fun onBluetoothDisconnected(device: LogPoseDevice) {
        if (currentDevice?.mac == device.mac) {
            currentDevice = null
            _state.value = AppState.STOPPED
            LogPoseLogger.i("ThamisCore: Bluetooth desconectado")
        }
    }

    fun ready() { _state.value = AppState.READY }
    fun startListening() { _state.value = AppState.LISTENING }
    fun stopListening() { _state.value = AppState.READY }
    fun setCallActive() { _state.value = AppState.CALL_ACTIVE }
    fun setIncomingCall() { _state.value = AppState.CALL_INCOMING }
    fun setCallEnding() { _state.value = AppState.CALL_ENDING }
    fun setWaitingSendConfirmation() { _state.value = AppState.WAITING_SEND_CONFIRMATION }
    
    fun getState(): AppState = _state.value

    suspend fun processEvent(event: Event) {
        LogPoseLogger.d("ThamisCore: Procesando evento: ${event.type}")
        
        // Pipeline de Inteligencia THAMIS
        when (event.type) {
            EventType.VOICE_COMMAND -> {
                val rawText = event.data as? String ?: return
                
                // 1. NLU (Comprensión) - Simulado por ahora en nlu
                
                // 2. Context & Memory
                val currentContext = ContextEngine.getActiveContext()
                
                // 3. Learning
                // learning.observeBehavior(true) 
                
                // 4. Decision & Safety
                // val result = decision.evaluate(intent, currentContext)
                // if (safety.isActionAllowed(result)) {
                //    voice.handleDecision(result)
                // }
            }
            EventType.DEVICE_CONNECTED -> {
                (event.data as? LogPoseDevice)?.let { onBluetoothConnected(it) }
            }
            EventType.DEVICE_DISCONNECTED -> {
                (event.data as? LogPoseDevice)?.let { onBluetoothDisconnected(it) }
            }
            else -> {
                eventBus.emit(event)
            }
        }
    }

    fun shutdown() {
        LogPoseLogger.i("ThamisCore: Apagado seguro del sistema.")
        _state.value = AppState.STOPPED
    }

    companion object {
        @Volatile
        private var INSTANCE: ThamisCore? = null

        fun getInstance(context: Context): ThamisCore {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ThamisCore(context).also { INSTANCE = it }
            }
        }

        /**
         * Limpia la instancia y el estado interno para pruebas unitarias.
         */
        fun resetForTesting() {
            synchronized(this) {
                INSTANCE?.shutdown()
                INSTANCE = null
            }
        }
    }
}
