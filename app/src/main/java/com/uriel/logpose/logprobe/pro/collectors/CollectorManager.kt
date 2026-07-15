package com.uriel.logpose.logprobe.collectors

import com.uriel.logpose.logprobe.common.ProbeGuard
import com.uriel.logpose.logprobe.common.ProbeLogger
import com.uriel.logpose.logprobe.common.ProbeSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Orquesta el ciclo de vida de todos los collectors durante una sesion.
 * No decide QUE se captura (eso es ProbeGuard); decide CUANDO cada
 * collector arranca y para.
 */
class CollectorManager(
    private val pollingCollectors: List<PollingCollector>,
    private val serviceBackedCollectors: List<ServiceBackedCollector>
) {
    private var scopeJob: Job? = null
    private var scope: CoroutineScope? = null

    fun startAll(session: ProbeSession) {
        ProbeGuard.startSession(session)

        val job = SupervisorJob()
        val currentScope = CoroutineScope(Dispatchers.Default + job)
        scopeJob = job
        scope = currentScope

        pollingCollectors.forEach { collector ->
            currentScope.launch {
                try {
                    collector.start()
                } catch (t: Throwable) {
                    ProbeLogger.e("Error iniciando collector de polling", t)
                }
            }
        }
        ProbeLogger.d("CollectorManager: sesion ${session.sessionId} iniciada con ${pollingCollectors.size} polling collectors")
    }

    /**
     * Detiene todo. Los ServiceBackedCollector no se "detienen" en el
     * sentido de desregistrar el Service del sistema (eso lo maneja
     * Android via el manifest / configuracion del usuario), pero al
     * llamar ProbeGuard.stopSession() dejan de producir eventos:
     * cualquier callback que llegue despues es rechazado.
     */
    fun stopAll() {
        pollingCollectors.forEach { it.stop() }
        scopeJob?.cancel()
        scopeJob = null
        scope = null
        ProbeGuard.stopSession()
        ProbeLogger.d("CollectorManager: sesion detenida, memoria de collectors liberada")
    }
}
