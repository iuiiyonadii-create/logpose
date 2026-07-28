package com.uriel.logpose.thamis.navigation.provider

/**
 * Proveedor de navegación legado para asegurar compatibilidad.
 */
class LegacyNavigationProvider : NavigationProvider {
    override fun open(): NavigationProviderResult {
        // Delegar al sistema legado
        return NavigationProviderResult(true, NavigationProviderType.LEGACY, "Success")
    }

    override fun navigateHome() = open()
    override fun navigateWork() = open()
    override fun navigate(destination: String) = open()
    override fun cancelNavigation() = open()
    override fun resumeNavigation() = open()
    override fun stopNavigation() = open()

    override fun isInstalled(): Boolean = true
    override fun isAvailable(): Boolean = true
    override fun supportsFeature(feature: NavigationFeature): Boolean = true

    override fun getProviderInfo(): NavigationProvider.ProviderInfo {
        return NavigationProvider.ProviderInfo(
            "Legacy", "1.0", NavigationProviderType.LEGACY, 
            NavigationFeature.values().toSet()
        )
    }
}
