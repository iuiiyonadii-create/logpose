package com.uriel.logpose.ui

sealed class Screen {
    object Dashboard : Screen()
    object Profile : Screen()
    object Music : Screen()
    object Voice : Screen()
    object Calls : Screen()
    object Messages : Screen()
    object Navigation : Screen()
    object Privacy : Screen()
    object Settings : Screen()
    object Help : Screen()
    object TripActive : Screen()
    object VoiceSlots : Screen()
}
