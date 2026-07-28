package com.uriel.logpose.thamis.navigation.provider

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Factory responsable de seleccionar automáticamente el mejor proveedor de navegación disponible.
 */
object NavigationProviderFactory {
    private const val TAG = "THAMIS_PROVIDER"

    private var providers = mutableListOf<NavigationProvider>()

    fun registerProvider(provider: NavigationProvider) {
        providers.add(provider)
    }

    fun getBestAvailableProvider(): NavigationProvider {
        // Prioridad: Google Maps > Waze > Legacy
        return providers.firstOrNull { it.isInstalled() && it.isAvailable() && it.getProviderInfo().type == NavigationProviderType.GOOGLE_MAPS }
            ?: providers.firstOrNull { it.isInstalled() && it.isAvailable() && it.getProviderInfo().type == NavigationProviderType.WAZE }
            ?: providers.firstOrNull { it.isAvailable() && it.getProviderInfo().type == NavigationProviderType.LEGACY }
            ?: NoProvider()
    }

    private class NoProvider : NavigationProvider {
        override fun open() = NavigationProviderResult(false, NavigationProviderType.NONE, "No provider available")
        override fun navigateHome() = open()
        override fun navigateWork() = open()
        override fun navigate(destination: String) = open()
        override fun cancelNavigation() = open()
        override fun resumeNavigation() = open()
        override fun stopNavigation() = open()
        override fun isInstalled() = false
        override fun isAvailable() = false
        override fun supportsFeature(feature: NavigationFeature) = false
        override fun getProviderInfo() = NavigationProvider.ProviderInfo("None", "0.0", NavigationProviderType.NONE, emptySet())
    }
}
