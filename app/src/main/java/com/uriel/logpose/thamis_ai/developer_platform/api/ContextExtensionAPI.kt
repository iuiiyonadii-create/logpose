package com.uriel.logpose.thamis_ai.developer_platform.api

/**
 * Specifically for accessing situational data.
 */
interface ContextExtensionAPI {
    fun getCurrentSituation(): String
    fun onSituationUpdated(situation: String)
}
