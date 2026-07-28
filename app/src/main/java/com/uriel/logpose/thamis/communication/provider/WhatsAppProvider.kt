package com.uriel.logpose.thamis.communication.provider

import android.content.Intent
import android.net.Uri
import com.uriel.logpose.core.app.AppContainer

/**
 * Proveedor de comunicación para WhatsApp.
 */
class WhatsAppProvider : CommunicationProvider {

    private val PACKAGE_NAME = "com.whatsapp"

    override fun callContact(contactName: String): CommunicationResult {
        // WhatsApp no permite iniciar llamadas de voz directamente vía Intent público fácilmente
        // pero podemos abrir el chat.
        return try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("whatsapp://send?text=Hola") // Placeholder
            intent.setPackage(PACKAGE_NAME)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            AppContainer.appContext.startActivity(intent)
            CommunicationResult(true, CommunicationProviderType.WHATSAPP, "WhatsApp chat opened for $contactName")
        } catch (e: Exception) {
            CommunicationResult(false, CommunicationProviderType.WHATSAPP, "Error: ${e.message}")
        }
    }

    override fun answerCall(): CommunicationResult {
        return CommunicationResult(false, CommunicationProviderType.WHATSAPP, "Feature not supported by deep-link")
    }

    override fun rejectCall(): CommunicationResult {
        return CommunicationResult(false, CommunicationProviderType.WHATSAPP, "Feature not supported by deep-link")
    }

    override fun sendMessage(contactName: String, message: String): CommunicationResult {
        return try {
            // Requiere el número en formato internacional
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://api.whatsapp.com/send?phone=$contactName&text=${Uri.encode(message)}")
            intent.setPackage(PACKAGE_NAME)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            AppContainer.appContext.startActivity(intent)
            CommunicationResult(true, CommunicationProviderType.WHATSAPP, "WhatsApp message sent to $contactName")
        } catch (e: Exception) {
            CommunicationResult(false, CommunicationProviderType.WHATSAPP, "Error: ${e.message}")
        }
    }

    override fun isAvailable(): Boolean {
        return try {
            AppContainer.appContext.packageManager.getPackageInfo(PACKAGE_NAME, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getProviderInfo(): CommunicationProvider.ProviderInfo {
        return CommunicationProvider.ProviderInfo(
            "WhatsApp",
            CommunicationProviderType.WHATSAPP,
            setOf(CommunicationFeature.TEXT_MESSAGE, CommunicationFeature.VOICE_CALL)
        )
    }
}
