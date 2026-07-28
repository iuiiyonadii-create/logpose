package com.uriel.logpose.thamis.navigation.provider

interface NavigationProvider {
    fun open(): NavigationProviderResult
    fun navigateHome(): NavigationProviderResult
    fun navigateWork(): NavigationProviderResult
    fun navigate(destination: String): NavigationProviderResult
    fun cancelNavigation(): NavigationProviderResult
    fun resumeNavigation(): NavigationProviderResult
    fun stopNavigation(): NavigationProviderResult
    
    fun isInstalled(): Boolean
    fun isAvailable(): Boolean
    fun supportsFeature(feature: NavigationFeature): Boolean
    
    fun getProviderInfo(): ProviderInfo
    
    data class ProviderInfo(
        val name: String,
        val version: String,
        val type: NavigationProviderType,
        val supportedFeatures: Set<NavigationFeature>
    )
}
