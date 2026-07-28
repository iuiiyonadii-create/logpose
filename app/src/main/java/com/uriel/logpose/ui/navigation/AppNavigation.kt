package com.uriel.logpose.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uriel.logpose.presentation.home.LogPoseScreen

/**
 * Main navigation controller for the application.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            LogPoseScreen()
        }
        composable("bluetooth") {
            // BluetoothScreen()
        }
        composable("settings") {
            // SettingsScreen()
        }
    }
}
