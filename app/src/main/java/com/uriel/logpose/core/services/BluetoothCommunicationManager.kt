package com.uriel.logpose.core.services

import android.content.Context
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

    private var communicationJob: Job? = null

    /**
     * Activa el canal SCO de forma inteligente.
     * Hardened v2.0: Soporte asíncrono para Handshake de Intercomunicador.
     */
    fun startCommunication() {
        LogPoseLogger.i("AudioEngine: Solicitando ruteo SCO Dinámico.")
        
        communicationJob?.cancel()
        
        // SINCRO CLAUDE: Reset de modo para limpiar buffers internos de Android
        audioManager.mode = AudioManager.MODE_NORMAL
        audioManager.isMicrophoneMute = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            routeToBluetoothS()
        } else {
            routeToBluetoothLegacy()
        }

        // FASE DE ESTABILIZACIÓN: Algunos intercoms (V6 Pro+) requieren que el SCO esté
        // abierto por un tiempo antes de que el micrófono capture audio útil.
        LogPoseLogger.d("AudioEngine: Ruteo SCO preparado. Esperando estabilidad de hardware...")
    }

    /**
     * SINCRO: Este método sí activa el modo "Llamada" solo cuando es estrictamente necesario (hablar).
     * Optimizado para Handshake de baja latencia.
     */
    fun enterFullCommunicationMode() {
        LogPoseLogger.d("AudioEngine: Entrando en Modo Comunicación Total.")
        
        // Algunos intercomunicadores de gama baja se desconectan si pasamos a 
        // MODE_IN_COMMUNICATION demasiado rápido tras el A2DP.
        scope.launch {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                delay(150) // Pequeño respiro para el firmware del casco
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
                _isScoActive.value = true
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
        _isScoActive.value = true
    }

    /**
     * SINCRO CLAUDE: Baja el modo a NORMAL para que Spotify tome el foco sin conflicto.
     */
    fun prepareForMusic() {
        if (audioManager.mode != AudioManager.MODE_NORMAL) {
            LogPoseLogger.i("AudioEngine: Bajando a MODE_NORMAL para dar paso a Spotify.")
            audioManager.mode = AudioManager.MODE_NORMAL
        }
    }

    /**
     * Restaura el modo de comunicación si SCO está activo.
     */
    fun restoreCommunication() {
        if (_isScoActive.value && audioManager.mode != AudioManager.MODE_IN_COMMUNICATION) {
            LogPoseLogger.i("AudioEngine: Restaurando MODE_IN_COMMUNICATION.")
            audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        }
    }

    /**
     * Libera el canal y vuelve al audio normal (A2DP o Speaker).
     * Hardened: Asegura que el hardware libere el micrófono para restaurar A2DP.
     */
    fun stopCommunication() {
        LogPoseLogger.i("AudioEngine: Cerrando canal de comunicación.")
        
        communicationJob?.cancel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            audioManager.clearCommunicationDevice()
        }
        
        audioManager.stopBluetoothSco()
        audioManager.isBluetoothScoOn = false
        
        // SINCRO: Pequeño delay antes de volver a MODE_NORMAL para evitar "pops" en el casco
        scope.launch {
            delay(100)
            audioManager.mode = AudioManager.MODE_NORMAL
            LogPoseLogger.d("AudioEngine: Canal liberado. A2DP Restaurado.")
        }
        
        _isScoActive.value = false
    }

    /**
     * El "Martillo" para intercomunicadores rebeldes.
     * Si el SCO no levanta, forzamos un ciclo de reinicio de ruteo.
     */
    fun hammerScoConnection() {
        LogPoseLogger.w("AudioEngine: Ejecutando Martillo de ruteo SCO.")
        stopCommunication()
        scope.launch {
            delay(500)
            startCommunication()
        }
    }
}
