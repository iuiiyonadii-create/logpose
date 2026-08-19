package com.uriel.logpose.thamis.communication.provider

/**
 * Factory para seleccionar el proveedor de comunicación adecuado.
 */
object CommunicationProviderFactory {
    private var providers = mutableListOf<CommunicationProvider>()

    fun registerProvider(provider: CommunicationProvider) {
        providers.add(provider)
    }

    fun getProvider(type: CommunicationProviderType): CommunicationProvider {
        return providers.find { it.getProviderInfo().type == type && it.isAvailable() } ?: NoProvider()
    }

    fun getDefaultProvider(): CommunicationProvider {
        // Prioridad: WhatsApp > Phone
        return providers.find { it.getProviderInfo().type == CommunicationProviderType.WHATSAPP && it.isAvailable() }
            ?: providers.find { it.getProviderInfo().type == CommunicationProviderType.PHONE && it.isAvailable() }
            ?: NoProvider()
    }

    private class NoProvider : CommunicationProvider {
        override fun callContact(contactName: String) = CommunicationResult(false, CommunicationProviderType.NONE, "No provider available")
        override fun answerCall() = CommunicationResult(false, CommunicationProviderType.NONE, "No provider available")
        override fun rejectCall() = CommunicationResult(false, CommunicationProviderType.NONE, "No provider available")
        override fun sendMessage(contactName: String, message: String) = CommunicationResult(false, CommunicationProviderType.NONE, "No provider available")
        override fun isAvailable() = false
        override fun getProviderInfo() = CommunicationProvider.ProviderInfo("None", CommunicationProviderType.NONE, emptySet())
    }
}
