package com.uriel.logpose.thamis.notification.classifier

import com.uriel.logpose.thamis.notification.model.NotificationCategory
import com.uriel.logpose.thamis.notification.model.NotificationContext

/**
 * Clasifica semánticamente las notificaciones basadas en el paquete y contenido.
 */
object NotificationClassifier {

    fun classify(context: NotificationContext): NotificationCategory {
        val pkg = context.appPackage.lowercase()
        val title = context.title?.lowercase() ?: ""
        val text = context.text?.lowercase() ?: ""

        return when {
            // Mensajería
            pkg.contains("whatsapp") || pkg.contains("telegram") || pkg.contains("mms") || pkg.contains("messaging") -> NotificationCategory.MESSAGE
            
            // Navegación
            pkg.contains("maps") || pkg.contains("waze") -> NotificationCategory.NAVIGATION
            
            // Delivery
            pkg.contains("pedidosya") || pkg.contains("rappi") || pkg.contains("mercadolibre") -> NotificationCategory.DELIVERY
            
            // Sistema / Batería
            pkg.contains("android.system") || title.contains("batería") || title.contains("battery") -> NotificationCategory.BATTERY
            
            // Emergencia (Semántica)
            title.contains("alerta") || text.contains("urgente") || text.contains("ayuda") -> NotificationCategory.EMERGENCY
            
            // Redes Sociales
            pkg.contains("instagram") || pkg.contains("facebook") || pkg.contains("tiktok") -> NotificationCategory.SOCIAL
            
            else -> NotificationCategory.UNKNOWN
        }
    }
}
