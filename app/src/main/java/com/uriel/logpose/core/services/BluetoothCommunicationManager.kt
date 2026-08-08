package com.uriel.logpose.core.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import com.uriel.logpose.core.compat.core.LogPoseLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * BluetoothCommunicationManager: El nuevo corazón de audio de LogPose.
 * Abandona la semántica de "Llamada" para usar "Comunicación Directa".
 * Mejorado (Misión #008): Detección física de enlace SCO para evitar clipping.
 */
class BluetoothCommunicationManager(private var context: Context) {

    private var audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    /**
     * SINCRO CLAUDE: Permite inyectar un contexto con atribución (attributionTag)
     * para que HyperOS no rechace las operaciones de audio.
     */
    fun updateContext(newContext: Context) {
        this.context = newContext
        this.audioManager = newContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    
    private val _isScoActive = MutableStateFlow(false)
    val isScoActive = _isScoActive.asStateFlow()

    private val _isScoPhysicallyConnected = MutableStateFlow(false)
    val isScoPhysicallyConnected = _isScoPhysicallyConnected.asStateFlow()

    private var communicationJob: Job? = null

    private val scoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val state = intent?.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1)
            when (state) {
                AudioManager.SCO_AUDIO_STATE_CONNECTED -> {
                    LogPoseLogger.i("AudioEngine: SCO Enlace Físico ESTABLECIDO.")
                    _isScoPhysicallyConnected.value = true
                }
                AudioManager.SCO_AUDIO_STATE_DISCONNECTED -> {
                    LogPoseLogger.d("AudioEngine: SCO Enlace Físico CERRADO.")
                    _isScoPhysicallyConnected.value = false
                }
            }
        }
    }

    init {
        context.registerReceiver(scoReceiver, IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED))
    }

    /**
     * Activa el canal SCO de forma inteligente.
     * Hardened v2.0: Soporte asíncrono para Handshake de Intercomunicador.
     */
    fun startCommunication() {
        LogPoseLogger.i("AudioEngine: Solicitando ruteo SCO Dinámico.")
        com.uriel.logpose.thamis.evolution.BluetoothIntelligence.recordEvent(
            com.uriel.logpose.thamis.evolution.BluetoothEventType.SCO_START_REQUEST
        )
        
        communicationJob?.cancel()
        
        // SINCRO CLAUDE: Reset de modo para limpiar buffers internos de Android
        audioManager.mode = AudioManager.MODE_NORMAL
        audioManager.isMicrophoneMute = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            routeToBluetoothS()
        } else {
            routeToBluetoothLegacy()
        }

        LogPoseLogger.d("AudioEngine: Ruteo SCO preparado. Esperando estabilidad de hardware...")
    }

    /**
     * SINCRO: Este método sí activa el modo "Llamada" solo cuando es estrictamente necesario (hablar).
     * Optimizado para Handshake de baja latencia.
     */
    fun enterFullCommunicationMode() {
        LogPoseLogger.d("AudioEngine: Entrando en Modo Comunicación Total.")
        
        scope.launch {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                delay(150) 
            }
            audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
            LogPoseLogger.i("AudioEngine: Hardware LOCK - Micrófono del casco EN VIVO.")
        }
    }

    fun exitFullCommunicationMode() {
        LogPoseLogger.d("AudioEngine: Saliendo de Modo Comunicación. Restaurando A2DP.")
        audioManager.mode = AudioManager.MODE_NORMAL
    }

    @androidx.annotation.RequiresApi(Build.VERSION_CODES.S)
    private fun routeToBluetoothS() {
        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        val scoDevice = devices.find { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO }
        
        if (scoDevice != null) {
            val result = audioManager.setCommunicationDevice(scoDevice)
            if (result) {
                LogPoseLogger.i("AudioEngine: Hardware LOCK en dispositivo SCO (API 31+).")
                com.uriel.logpose.thamis.evolution.BluetoothIntelligence.recordEvent(
                    com.uriel.logpose.thamis.evolution.BluetoothEventType.SCO_CONNECTED,
                    "API_31"
                )
                _isScoActive.value = true
                // En API 31+, el setCommunicationDevice es casi inmediato, pero el broadcast sigue siendo la verdad
            } else {
                LogPoseLogger.e("AudioEngine: Falló setCommunicationDevice.")
            }
        } else {
            LogPoseLogger.w("AudioEngine: No se encontró dispositivo SCO disponible. Forzando modo normal para A2DP.")
            audioManager.mode = AudioManager.MODE_NORMAL
            _isScoActive.value = false
        }
    }

    private fun routeToBluetoothLegacy() {
        audioManager.startBluetoothSco()
        audioManager.isBluetoothScoOn = true
        LogPoseLogger.i("AudioEngine: Solicitado SCO vía API Legacy.")
        com.uriel.logpose.thamis.evolution.BluetoothIntelligence.recordEvent(
            com.uriel.logpose.thamis.evolution.BluetoothEventType.SCO_CONNECTED,
            "LEGACY"
        )
        _isScoActive.value = true
    }

    fun prepareForMusic() {
        if (audioManager.mode != AudioManager.MODE_NORMAL) {
            LogPoseLogger.i("AudioEngine: Bajando a MODE_NORMAL para dar paso a Spotify.")
            audioManager.mode = AudioManager.MODE_NORMAL
        }
    }

    fun restoreCommunication() {
        if (_isScoActive.value && audioManager.mode != AudioManager.MODE_IN_COMMUNICATION) {
            LogPoseLogger.i("AudioEngine: Restaurando MODE_IN_COMMUNICATION.")
            audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        }
    }

    fun stopCommunication() {
        LogPoseLogger.i("AudioEngine: Cerrando canal de comunicación.")
        
        communicationJob?.cancel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            audioManager.clearCommunicationDevice()
        }
        
        audioManager.stopBluetoothSco()
        audioManager.isBluetoothScoOn = false
        
        scope.launch {
            delay(100)
            audioManager.mode = AudioManager.MODE_NORMAL
            LogPoseLogger.d("AudioEngine: Canal liberado. A2DP Restaurado.")
        }
        
        _isScoActive.value = false
        _isScoPhysicallyConnected.value = false
    }

    fun hammerScoConnection() {
        LogPoseLogger.w("AudioEngine: Ejecutando Martillo de ruteo SCO.")
        stopCommunication()
        scope.launch {
            delay(500)
            startCommunication()
        }
    }
}
