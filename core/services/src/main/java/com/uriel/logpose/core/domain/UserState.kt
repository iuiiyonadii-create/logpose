package com.uriel.logpose.core.domain

/**
 * FASE 26.1 — LOGPOSE PRODUCT IMPLEMENTATION FOUNDATION
 * FASE 3: CORE MODULE
 */
data class UserState(
    val isRiding: Boolean = false,
    val isPrivacyModeActive: Boolean = false,
    val activeCall: String? = null,
    val currentVolume: Int = 0
)
