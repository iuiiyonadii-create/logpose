package com.uriel.logpose.logprobe.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.uriel.logpose.logprobe.collectors.AccessibilityCollector
import com.uriel.logpose.logprobe.common.ProbeGuard
import com.uriel.logpose.logprobe.common.ProbeLogger

/**
 * Servicio de Accessibility de LogProbe. Lectura unicamente: nunca llama
 * performAction(), nunca despacha gestos, nunca interactua con las apps
 * que observa. Su unico rol es reenviar eventos a AccessibilityCollector,
 * que a su vez los filtra con ProbeGuard antes de construir cualquier
 * snapshot.
 *
 * El servicio queda declarado en el manifest y puede estar habilitado
 * a nivel de Ajustes de Accesibilidad del sistema en cualquier momento,
 * pero eso NO implica captura continua: si no hay ProbeSession activa
 * (ProbeGuard.hasActiveSession() == false), cada evento entrante se
 * descarta de inmediato sin construir snapshot ni tocar memoria.
 */
class AccessibilityProbeService : AccessibilityService() {

    private val collector = AccessibilityCollector

    override fun onServiceConnected() {
        super.onServiceConnected()
        collector.onServiceConnected()
        ProbeLogger.d("AccessibilityProbeService conectado")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (!ProbeGuard.hasActiveSession()) return
        collector.onAccessibilityEvent(event)
    }

    override fun onInterrupt() {
        ProbeLogger.w("AccessibilityProbeService interrumpido por el sistema")
    }

    override fun onUnbind(intent: android.content.Intent?): Boolean {
        collector.onServiceDisconnected()
        ProbeLogger.d("AccessibilityProbeService desconectado")
        return super.onUnbind(intent)
    }
}
