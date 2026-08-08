package com.uriel.logpose.features.voice

import android.os.Debug
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.uriel.logpose.domain.models.VoiceState
import com.uriel.logpose.domain.models.VoiceStatus
import com.uriel.logpose.domain.repositories.VoiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * ENDURANCE TEST - Hallazgo #2
 * 
 * Valida la estabilidad del VoiceManager durante ciclos extensos.
 * Verifica fugas de memoria, hilos y estados huérfanos.
 */
@RunWith(AndroidJUnit4::class)
class VoiceManagerEnduranceTest {

    private class VoiceRepositorySpy : VoiceRepository {
        var isListening = false
        var totalStarts = 0
        var totalStops = 0

        override val voiceState: StateFlow<VoiceStatus> = MutableStateFlow(VoiceStatus(VoiceState.IDLE))

        override fun startListening() {
            isListening = true
            totalStarts++
        }

        override fun stopListening() {
            isListening = false
            totalStops++
        }

        override fun resetStateForRestart() {
            isListening = false
        }
    }

    private val repositorySpy = VoiceRepositorySpy()
    private val mainHandler = Handler(Looper.getMainLooper())

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        mainHandler.post {
            VoiceManager.initialize(context, repositorySpy)
        }
        Thread.sleep(1000)
    }

    @Test
    fun enduranceHandshakeValidation() {
        // Ejecutamos una ráfaga de 1000 ciclos de estrés
        // En una prueba de 8 horas, esto se repetiría periódicamente
        val cycles = 1000
        var raceConditionDetected = false
        
        val initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        val initialThreads = Thread.activeCount()
        
        Log.i("Endurance_H2", "Iniciando ráfaga de $cycles ciclos. Memoria inicial: ${initialMemory/1024} KB. Hilos: $initialThreads")

        for (i in 1..cycles) {
            // 1. Stress Start/Stop
            mainHandler.post { VoiceManager.start() }
            
            // Retardo aleatorio para intentar pillar al Job en medio del delay(1000)
            val jitter = (10L..1200L).random()
            Thread.sleep(jitter)
            
            mainHandler.post { VoiceManager.stop() }
            
            // 2. Wait for completion
            Thread.sleep(200)
            
            // 3. Verificación de Inconsistencia (Hallazgo #2)
            // Si el sistema se detuvo (stop), el repositorio NO puede estar escuchando.
            if (repositorySpy.isListening) {
                raceConditionDetected = true
                Log.e("Endurance_H2", "FALLO CRÍTICO: Ciclo $i - Micrófono quedó ABIERTO tras Stop.")
                break
            }

            if (i % 100 == 0) {
                val currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
                val currentThreads = Thread.activeCount()
                Log.d("Endurance_H2", "Ciclo $i | RAM: ${currentMemory/1024} KB | Threads: $currentThreads")
            }
        }

        val finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        val memoryGrowth = finalMemory - initialMemory
        
        Log.i("Endurance_H2", "--- REPORTE FINAL ---")
        Log.i("Endurance_H2", "Crecimiento RAM: ${memoryGrowth/1024} KB")
        Log.i("Endurance_H2", "Hilos Finales: ${Thread.activeCount()}")
        
        assertFalse("Se detectó la condición de carrera del Hallazgo #2 durante el endurance.", raceConditionDetected)
        
        // Verificamos que no haya un crecimiento descontrolado de memoria (>10MB en 1000 ciclos es sospechoso)
        assertFalse("Posible Memory Leak detectado: Crecimiento de ${memoryGrowth/1024} KB", memoryGrowth > 10 * 1024 * 1024)
    }
}
