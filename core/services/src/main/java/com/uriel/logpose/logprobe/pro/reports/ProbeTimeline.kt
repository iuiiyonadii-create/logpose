package com.uriel.logpose.logprobe.reports

import com.uriel.logpose.logprobe.common.ProbeGuard
import com.uriel.logpose.logprobe.common.ProbeLogger
import com.uriel.logpose.logprobe.models.ProbeEvent
import java.util.Collections

/**
 * Almacen en memoria, ordenado por tiempo, de los eventos aprobados
 * durante la sesion activa. NUNCA recibe un evento directamente desde
 * un collector: todo el que llega aca ya fue construido con datos
 * que pasaron por ProbeGuard.
 *
 * Vive solo mientras dura la sesion: `clear()` se llama obligatoriamente
 * al hacer Stop Probe (ver ProbeViewModel / CollectorManager).
 */
object ProbeTimeline {

    private val events = Collections.synchronizedList(mutableListOf<ProbeEvent>())

    fun record(event: ProbeEvent) {
        if (!ProbeGuard.hasActiveSession()) {
            ProbeLogger.w("Evento descartado: no hay sesion activa (${event.type})")
            return
        }
        val session = ProbeGuard.currentSession()
        if (session == null || session.sessionId != event.sessionId) {
            ProbeLogger.w("Evento descartado: no pertenece a la sesion activa")
            return
        }
        events.add(event)
    }

    fun snapshot(): List<ProbeEvent> = synchronized(events) { events.toList() }

    fun clear() {
        synchronized(events) { events.clear() }
    }
}
