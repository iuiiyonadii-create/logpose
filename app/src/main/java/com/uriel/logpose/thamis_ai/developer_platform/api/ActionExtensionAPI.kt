package com.uriel.logpose.thamis_ai.developer_platform.api

/**
 * Specifically for requesting system actions (volume, music).
 */
interface ActionExtensionAPI {
    fun requestMediaAction(action: String)
    fun requestVolumeAction(direction: String)
}
