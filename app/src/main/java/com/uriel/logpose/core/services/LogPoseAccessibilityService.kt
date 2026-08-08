package com.uriel.logpose.core.services

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * LogPoseAccessibilityService: Automatiza interacciones en apps de terceros.
 * Específicamente diseñado para presionar el botón "Enviar" de WhatsApp.
 */
class LogPoseAccessibilityService : AccessibilityService() {

    companion object {
        var isPendingAutomation = false
        private const val WHATSAPP_PACKAGE = "com.whatsapp"
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (!isPendingAutomation) return
        
        val packageName = event.packageName?.toString()
        if (packageName != WHATSAPP_PACKAGE) return

        val rootNode = rootInActiveWindow ?: return
        
        // Buscamos el botón de enviar. WhatsApp suele usar un ImageButton sin ID de texto pero con ID de recurso.
        // También podemos buscar por descripción de contenido.
        findAndClickSendButton(rootNode)
    }

    private fun findAndClickSendButton(node: AccessibilityNodeInfo) {
        // Opción 1: Buscar por ID de recurso común en WhatsApp
        val sendButtons = node.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send")
        if (sendButtons.isNotEmpty()) {
            val btn = sendButtons[0]
            if (btn.isEnabled && btn.isClickable) {
                btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                LogPoseLogger.i("Accessibility: Botón 'Enviar' presionado automáticamente por ID.")
                isPendingAutomation = false
                return
            }
        }

        // Opción 2: Recorrido recursivo buscando descripción de contenido "Enviar" o similar
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val desc = child.contentDescription?.toString()?.lowercase()
            if (desc == "enviar" || desc == "send") {
                child.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                LogPoseLogger.i("Accessibility: Botón 'Enviar' presionado automáticamente por descripción.")
                isPendingAutomation = false
                return
            }
            findAndClickSendButton(child)
        }
    }

    override fun onInterrupt() {
        LogPoseLogger.w("Accessibility: Servicio interrumpido.")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        LogPoseLogger.i("Accessibility: LogPose Automation Service CONECTADO.")
    }
}
