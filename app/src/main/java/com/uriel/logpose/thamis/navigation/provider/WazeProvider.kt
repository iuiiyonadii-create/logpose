package com.uriel.logpose.thamis.navigation.provider

import android.content.Intent
import android.net.Uri
import com.uriel.logpose.core.app.LogPoseApplication

/**
 * Encapsula toda la integración con Waze.
 */
class WazeProvider : NavigationProvider {

    private val PACKAGE_NAME = "com.waze"

    override fun open(): NavigationProviderResult {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("waze://"))
            intent.setPackage(PACKAGE_NAME)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            LogPoseApplication.instance.startActivity(intent)
            NavigationProviderResult(true, NavigationProviderType.WAZE, "Waze Opened")
        } catch (e: Exception) {
            NavigationProviderResult(false, NavigationProviderType.WAZE, "Error: ${e.message}")
        }
    }

    override fun navigateHome(): NavigationProviderResult = navigate("Home")
    override fun navigateWork(): NavigationProviderResult = navigate("Work")

    override fun navigate(destination: String): NavigationProviderResult {
        return try {
            val url = "waze://?q=$destination&navigate=yes"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.setPackage(PACKAGE_NAME)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            LogPoseApplication.instance.startActivity(intent)
            NavigationProviderResult(true, NavigationProviderType.WAZE, "Navigating to $destination with Waze")
        } catch (e: Exception) {
            NavigationProviderResult(false, NavigationProviderType.WAZE, "Error: ${e.message}")
        }
    }

    override fun cancelNavigation(): NavigationProviderResult = open()
    override fun resumeNavigation() = open()
    override fun stopNavigation() = open()

    override fun isInstalled(): Boolean {
        return try {
            LogPoseApplication.instance.packageManager.getPackageInfo(PACKAGE_NAME, 0)
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
            NavigationFeature.CANCEL_ROUTE -> false
            NavigationFeature.RESUME_ROUTE -> false
            NavigationFeature.SEARCH_POI -> true
        }
    }

    override fun getProviderInfo(): NavigationProvider.ProviderInfo {
        return NavigationProvider.ProviderInfo(
            "Waze", "1.0", NavigationProviderType.WAZE, 
            setOf(
                NavigationFeature.OPEN_APP, 
                NavigationFeature.NAVIGATE_HOME, 
                NavigationFeature.NAVIGATE_WORK, 
                NavigationFeature.NAVIGATE_DESTINATION,
                NavigationFeature.SEARCH_POI
            )
        )
    }
}
