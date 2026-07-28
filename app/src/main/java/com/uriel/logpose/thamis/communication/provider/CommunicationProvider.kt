package com.uriel.logpose.thamis.communication.provider

/**
 * Interfaz base para proveedores de comunicación (Llamadas, Mensajes).
 */
interface CommunicationProvider {
    fun callContact(contactName: String): CommunicationResult
    fun answerCall(): CommunicationResult
    fun rejectCall(): CommunicationResult
    fun sendMessage(contactName: String, message: String): CommunicationResult
    
    fun isAvailable(): Boolean
    fun getProviderInfo(): ProviderInfo

    data class ProviderInfo(
        val name: String,
        val type: CommunicationProviderType,
        val supportedFeatures: Set<CommunicationFeature>
    )
}

enum class CommunicationProviderType {
    PHONE,
    WHATSAPP,
    TELEGRAM,
    NONE
}

enum class CommunicationFeature {
    VOICE_CALL,
    VIDEO_CALL,
    TEXT_MESSAGE,
    VOICE_MESSAGE
}

data class CommunicationResult(
    val success: Boolean,
    val provider: CommunicationProviderType,
    val message: String
)
