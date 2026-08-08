package com.uriel.logpose.features.voice

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.uriel.logpose.domain.repositories.VoiceRepository
import com.uriel.logpose.domain.models.VoiceStatus
import com.uriel.logpose.domain.models.VoiceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import android.os.Handler
import android.os.Looper
import android.util.Log

/**
 * Prueba de estrés para reproducir la condición de carrera en el Handshake de VoiceManager.
 * 
 * OBJETIVO: Demostrar que una llamada a stop() puede ser sobrescrita por un start() previo
 * debido al uso de Handler.postDelayed no cancelable.
 */
@RunWith(AndroidJUnit4::class)
class VoiceManagerRaceTest {

    private class VoiceRepositorySpy : VoiceRepository {
        var lastCallWasStart = false
        var startCount = 0
        var stopCount = 0

        override val voiceState: StateFlow<VoiceStatus> = MutableStateFlow(VoiceStatus(VoiceState.IDLE))

        override fun startListening() {
            startCount++
            lastCallWasStart = true
            Log.d("VoiceRaceTest", "[SPY] startListening() ejecutado. Total: $startCount")
        }

        override fun stopListening() {
            stopCount++
            lastCallWasStart = false
            Log.d("VoiceRaceTest", "[SPY] stopListening() ejecutado. Total: $stopCount")
        }

        override fun resetStateForRestart() {
            lastCallWasStart = false
        }
    }

    private val repositorySpy = VoiceRepositorySpy()
    private val mainHandler = Handler(Looper.getMainLooper())

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Inyectamos el Spy en el Singleton VoiceManager
        mainHandler.post {
            VoiceManager.initialize(context, repositorySpy)
        }
        Thread.sleep(500)
    }

    @Test
    fun reproduceHandshakeOverlap() {
        val iterations = 1000
        var failures = 0
        
        Log.i("VoiceRaceTest", "Iniciando prueba de 1000 iteraciones...")

        for (i in 1..iterations) {
            // 1. Iniciamos el sistema (esto encola un bloque para T+1000ms)
            mainHandler.post { VoiceManager.start() }
            
            // 2. Simulamos una interrupción rápida (entre 100 y 400ms)
            val interruptionDelay = (100L..400L).random()
            Thread.sleep(interruptionDelay)
            
            // 3. Detenemos el sistema
            // stop() encola un bloque para T+800ms para resetear isStopping
            mainHandler.post { VoiceManager.stop() }
            
            // 4. Esperamos a que pasen todos los retardos (1500ms total)
            Thread.sleep(1500)
            
            // 5. AUDITORÍA: Si el repositorio está en modo 'Start', el bug ocurrió.
            // Porque la última acción intencional fue stop().
            if (repositorySpy.lastCallWasStart) {
                failures++
                Log.e("VoiceRaceTest", "¡ERROR DETECTADO! Iteración $i: El micrófono quedó ABIERTO tras un stop().")
                
                // Forzamos stop real para limpiar la siguiente iteración
                mainHandler.post { VoiceManager.stop() }
                Thread.sleep(500)
            }

            if (i % 50 == 0) {
                Log.i("VoiceRaceTest", "Progreso: $i / $iterations. Fallos acumulados: $failures")
            }
        }

        Log.i("VoiceRaceTest", "===========================================")
        Log.i("VoiceRaceTest", "RESULTADO FINAL: $failures fallos en $iterations pruebas.")
        Log.i("VoiceRaceTest", "===========================================")
        
        assertFalse("Condición de carrera confirmada: El Handshake Overlap ocurrió $failures veces.", failures > 0)
    }
}
