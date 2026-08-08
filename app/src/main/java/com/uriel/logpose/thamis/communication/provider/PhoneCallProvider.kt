package com.uriel.logpose.thamis.communication.provider

import android.content.Intent
import android.net.Uri
import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Proveedor de llamadas telefónicas estándar de Android.
 */
class PhoneCallProvider : CommunicationProvider {

    override fun callContact(contactName: String): CommunicationResult {
        return try {
            // En un sistema real, primero buscaríamos el número del contacto.
            // Por ahora, simulamos que contactName es el número o usamos un placeholder.
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$contactName")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            LogPoseApplication.instance.startActivity(intent)
            CommunicationResult(true, CommunicationProviderType.PHONE, "Calling $contactName")
        } catch (e: Exception) {
            LogPoseLogger.e("PhoneCallProvider: Error calling $contactName - ${e.message}")
            CommunicationResult(false, CommunicationProviderType.PHONE, "Error: ${e.message}")
        }
    }

    override fun answerCall(): CommunicationResult {
        // Responder llamadas programáticamente requiere ser el Default Dialer o usar Accessibility/TelecomRoot.
        return CommunicationResult(false, CommunicationProviderType.PHONE, "Answer call not implemented yet")
    }

    override fun rejectCall(): CommunicationResult {
        return CommunicationResult(false, CommunicationProviderType.PHONE, "Reject call not implemented yet")
    }

    override fun sendMessage(contactName: String, message: String): CommunicationResult {
        return try {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("smsto:$contactName")
            intent.putExtra("sms_body", message)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            LogPoseApplication.instance.startActivity(intent)
            CommunicationResult(true, CommunicationProviderType.PHONE, "SMS prepared for $contactName")
        } catch (e: Exception) {
            CommunicationResult(false, CommunicationProviderType.PHONE, "Error: ${e.message}")
        }
    }

    override fun isAvailable(): Boolean = true

    override fun getProviderInfo(): CommunicationProvider.ProviderInfo {
        return CommunicationProvider.ProviderInfo(
            "Android Phone",
            CommunicationProviderType.PHONE,
            setOf(CommunicationFeature.VOICE_CALL, CommunicationFeature.TEXT_MESSAGE)
        )
    }
}
