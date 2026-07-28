package com.uriel.logpose.thamis_ai.developer_platform.api

/**
 * Interface definition for exposed THAMIS capabilities.
 */
interface PublicAPI {
    fun onCommand(text: String)
    fun onStateChanged(state: String)
}
