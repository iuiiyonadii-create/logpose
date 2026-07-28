package com.uriel.logpose.thamis.performance.validation

import com.uriel.logpose.thamis.performance.profiler.PerformanceProfiler
import com.uriel.logpose.thamis.performance.report.PerformanceReportGenerator

/**
 * Suite de simulación para validar la medición bajo alta carga cognitiva.
 */
class PerformanceStressTest {

    fun runScenario() {
        val modules = listOf("Navigation", "Multimedia", "Planning", "Communication", "Dialog")
        
        repeat(20) {
            modules.forEach { module ->
                PerformanceProfiler.startOperation(module, "CognitiveProcess")
                // Simulación de carga
                Thread.sleep((10..100).random().toLong())
                PerformanceProfiler.finishOperation(module, "CognitiveProcess")
            }
        }
        
        // Simular algunos errores y latencia alta
        PerformanceProfiler.startOperation("Navigation", "GPS_Handshake")
        Thread.sleep(600)
        PerformanceProfiler.finishOperation("Navigation", "GPS_Handshake", "TIMEOUT")
        PerformanceProfiler.recordError("Communication", "Provider connection lost")
        
        PerformanceReportGenerator.generate()
    }
}
