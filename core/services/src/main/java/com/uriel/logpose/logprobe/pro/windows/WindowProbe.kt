package com.uriel.logpose.logprobe.windows

import com.uriel.logpose.logprobe.models.ProbeEventType
import com.uriel.logpose.logprobe.models.WindowSnapshot
import com.uriel.logpose.logprobe.reports.ProbeTimeline

/**
 * DECISION DE ARQUITECTURA: no existe un WindowCollector de polling
 * independiente. La ventana/actividad activa ya se obtiene de forma
 * mas precisa a partir de TYPE_WINDOW_STATE_CHANGED en
 * AccessibilityCollector (ver ese archivo), sin necesitar el permiso
 * especial PACKAGE_USAGE_STATS. WindowProbe es una capa de lectura
 * sobre esos eventos ya almacenados en ProbeTimeline.
 */
object WindowProbe {

    fun currentWindowEvents(): List<WindowSnapshot> =
        ProbeTimeline.snapshot()
            .filter { it.type == ProbeEventType.WINDOW_CHANGED }
            .mapNotNull { it.snapshot as? WindowSnapshot }

    fun lastWindow(): WindowSnapshot? = currentWindowEvents().lastOrNull()
}
