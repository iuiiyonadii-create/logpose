package com.uriel.logpose.thamis.navigation.provider

data class NavigationProviderResult(
    val success: Boolean,
    val provider: NavigationProviderType,
    val reason: String,
    val executionTimeMs: Long = 0
)
