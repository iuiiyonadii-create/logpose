package com.uriel.logpose.thamis.security.monitor

import com.uriel.logpose.thamis.security.model.ResourceType

/**
 * Proporciona visibilidad al usuario sobre el uso de recursos.
 */
object SecurityTransparencyManager {

    fun getUsageExplanation(resource: ResourceType): String {
        return when (resource) {
            ResourceType.MICROPHONE -> "El micrófono se utiliza exclusivamente para reconocer comandos de voz."
            ResourceType.BLUETOOTH -> "Bluetooth se utiliza para mantener la conexión con el intercomunicador del casco."
            ResourceType.LOCATION -> "La ubicación se utiliza para la navegación y el cálculo de seguridad por velocidad."
            else -> "Este recurso se utiliza para el funcionamiento normal de THAMIS."
        }
    }
}
