package com.uriel.logpose.thamis_ai.developer_platform.api

/**
 * Specifically for connecting third-party platforms.
 */
interface IntegrationAPI {
    fun connectService(serviceName: String)
    fun onServiceConnected(status: Boolean)
}
