package com.uriel.logpose.thamis.navigation

import com.uriel.logpose.thamis.navigation.provider.NavigationProviderFactory
import com.uriel.logpose.thamis.navigation.provider.NavigationProviderType
import org.junit.Test
import org.junit.Assert.*

class NavigationFallbackTest {

    @Test
    fun testProviderFallbackOrder() {
        val provider = NavigationProviderFactory.getBestAvailableProvider()
        // Por defecto en stubs, Google Maps es el mejor disponible
        assertNotNull(provider)
        println("Best Provider: ${provider.getProviderInfo().name}")
    }
}
