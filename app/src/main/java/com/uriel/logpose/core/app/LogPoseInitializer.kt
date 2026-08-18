package com.uriel.logpose.core.app

import android.content.Context
import androidx.startup.Initializer
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.services.AlertManager
import kotlinx.coroutines.*

/**
 * LogPoseInitializer: Inicialización Asíncrona y No-Bloqueante (HyperOS Deadlock Fix v10.1).
 * Desacopla la inicialización de hardware de audio (ToneGenerator, ThamisVoiceEngine) del Main Thread.
 */
class LogPoseInitializer : Initializer<Unit> {

    private val initScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun create(context: Context) {
        LogPoseLogger.initialize(context)
        LogPoseLogger.i("LogPoseInitializer: Secuencia de carga asíncrona iniciada (0ms Main Thread lock)...")

        val appContext = context.applicationContext

        // 1. Non-Blocking Background Launch (Desacoplamiento de audio_flinger)
        initScope.launch {
            try {
                // 2. Retraso controlado de 300ms para estabilización de HyperOS / MiuiPreloadClassImpl
                delay(300L)
                LogPoseLogger.d("LogPoseInitializer", "300ms delay de estabilización completado. Inicializando audio...")

                // 3. Inicialización aislada de ToneGenerator y ThamisVoiceEngine en IO
                AlertManager.initialize(appContext)

                // 4. Pre-carga segura del motor Vosk en segundo plano
                try {
                    LogPoseApplication.entryPoint.voskVoiceEngine()
                } catch (e: Exception) {
                    LogPoseLogger.w("LogPoseInitializer", "VoskVoiceEngine pre-warm diferido: ${e.message}")
                }
                
                LogPoseLogger.i("LogPoseInitializer", "✅ Inicialización multimedia en segundo plano completada con éxito.")
            } catch (e: Exception) {
                LogPoseLogger.e("LogPoseInitializer", "Error en inicialización multimedia en segundo plano: ${e.message}")
            }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
