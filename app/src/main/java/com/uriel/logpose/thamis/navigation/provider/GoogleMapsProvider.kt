package com.uriel.logpose.thamis.navigation.provider

import android.content.Intent
import com.uriel.logpose.core.app.LogPoseApplication
import androidx.core.net.toUri

/**
 * Encapsula toda la integración con Google Maps.
 */
class GoogleMapsProvider : NavigationProvider {
    
    private val packageName = "com.google.android.apps.maps"

    override fun open(): NavigationProviderResult {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, "geo:0,0".toUri())
            intent.setPackage(packageName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            LogPoseApplication.instance.startActivity(intent)
            NavigationProviderResult(success = true, provider = NavigationProviderType.GOOGLE_MAPS, reason = "Maps Opened")
        } catch (e: Exception) {
            NavigationProviderResult(success = false, provider = NavigationProviderType.GOOGLE_MAPS, reason = "Error: ${e.message}")
        }
    }

    override fun navigateHome(): NavigationProviderResult = navigate("Home")
    override fun navigateWork(): NavigationProviderResult = navigate("Work")

    override fun navigate(destination: String): NavigationProviderResult {
        return try {
            val gmmIntentUri = "google.navigation:q=$destination".toUri()
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage(packageName)
            mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            LogPoseApplication.instance.startActivity(mapIntent)
            NavigationProviderResult(success = true, provider = NavigationProviderType.GOOGLE_MAPS, reason = "Navigating to $destination")
        } catch (e: Exception) {
            NavigationProviderResult(success = false, provider = NavigationProviderType.GOOGLE_MAPS, reason = "Error: ${e.message}")
        }
    }

    override fun cancelNavigation(): NavigationProviderResult {
        // Google Maps no permite cancelar vía Intent fácilmente sin un broadcast específico o Accessibility.
        // Por ahora, solo abrimos la app.
        return open()
    }

    override fun resumeNavigation() = open()
    override fun stopNavigation() = cancelNavigation()

    override fun isInstalled(): Boolean {
        return try {
            LogPoseApplication.instance.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun isAvailable(): Boolean = isInstalled()
    
    override fun supportsFeature(feature: NavigationFeature): Boolean {
        return when(feature) {
            NavigationFeature.OPEN_APP -> true
            NavigationFeature.NAVIGATE_HOME -> true
            NavigationFeature.NAVIGATE_WORK -> true
            NavigationFeature.NAVIGATE_DESTINATION -> true
            NavigationFeature.CANCEL_ROUTE -> false // No soportado por Intent
            NavigationFeature.RESUME_ROUTE -> false
            NavigationFeature.SEARCH_POI -> true
        }
    }

    override fun getProviderInfo(): NavigationProvider.ProviderInfo {
        return NavigationProvider.ProviderInfo(
            "Google Maps", "1.0", NavigationProviderType.GOOGLE_MAPS, 
            setOf(
                NavigationFeature.OPEN_APP, 
                NavigationFeature.NAVIGATE_HOME, 
                NavigationFeature.NAVIGATE_WORK, 
                NavigationFeature.NAVIGATE_DESTINATION,
                NavigationFeature.SEARCH_POI,
            )
        )
    }
}
