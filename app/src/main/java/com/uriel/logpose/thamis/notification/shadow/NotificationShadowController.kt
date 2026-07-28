package com.uriel.logpose.thamis.notification.shadow

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.notification.classifier.NotificationClassifier
import com.uriel.logpose.thamis.notification.priority.NotificationPriorityEngine
import com.uriel.logpose.thamis.notification.model.*
import com.uriel.logpose.thamis.notification.security.NotificationSafetyGate
import com.uriel.logpose.thamis.notification.audit.*
import com.uriel.logpose.thamis.world.context.WorldContextEngine
import com.uriel.logpose.thamis.world.model.driving

/**
 * Orquestador en modo Shadow del dominio de notificaciones.
 */
object NotificationShadowController {

    fun process(context: NotificationContext) {
        // 1. Clasificar
        val category = NotificationClassifier.classify(context)
        
        // 2. Determinar Prioridad
        val priority = NotificationPriorityEngine.determinePriority(category)
        
        // 3. Consultar Estado del Mundo
        val worldSnapshot = WorldContextEngine.getCurrentState()
        
        // 4. Validar Seguridad
        val action = NotificationSafetyGate.determineAction(priority, worldSnapshot)
        
        val decision = NotificationDecision(
            category = category,
            priority = priority,
            action = action,
            reason = "Auto-class: $category, Context speed: ${worldSnapshot.driving.speedKmh}",
            confidence = 0.9f
        )
        
        // 5. Registrar Traza
        NotificationAudit.record(NotificationTrace(context, decision))
        
        // 6. Logcat
        LogPoseLogger.i("[THAMIS_NOTIFICATION] App: ${context.appPackage} | Cat: $category | Priority: $priority | Decision: $action")
    }
}
