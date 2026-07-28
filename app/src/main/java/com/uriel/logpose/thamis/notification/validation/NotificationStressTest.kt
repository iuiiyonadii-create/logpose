package com.uriel.logpose.thamis.notification.validation

import com.uriel.logpose.thamis.notification.model.NotificationContext
import com.uriel.logpose.thamis.notification.shadow.NotificationShadowController

/**
 * Suite de pruebas de estrés para el motor cognitivo de notificaciones.
 */
class NotificationStressTest {

    fun run() {
        val scenarios = listOf(
            NotificationContext(appPackage = "com.whatsapp", title = "Juan", text = "Hola"),
            NotificationContext(appPackage = "com.android.system", title = "Batería baja", text = "15% restante"),
            NotificationContext(appPackage = "com.instagram.android", title = "Maria", text = "Le dio me gusta a tu foto"),
            NotificationContext(appPackage = "com.google.android.apps.maps", title = "Gire a la derecha", text = "50 metros"),
            NotificationContext(appPackage = "com.urgente.app", title = "ALERTA", text = "Sismo detectado")
        )

        scenarios.forEach { 
            NotificationShadowController.process(it)
        }
    }
}
