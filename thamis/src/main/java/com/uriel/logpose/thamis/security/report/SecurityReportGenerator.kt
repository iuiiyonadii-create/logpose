package com.uriel.logpose.thamis.security.report

import com.uriel.logpose.thamis.security.model.*

/**
 * Genera informes de salud de seguridad y privacidad.
 */
object SecurityReportGenerator {

    fun generate(): SecurityReport {
        val status = mutableMapOf<ResourceType, String>()
        status[ResourceType.MICROPHONE] = "Normal"
        status[ResourceType.BLUETOOTH] = "Normal"
        status[ResourceType.LOCATION] = "Normal"
        status[ResourceType.NETWORK] = "Normal"

        return SecurityReport(
            summary = "THAMIS SECURITY REPORT - Status OK",
            statusMap = status,
            alerts = emptyList()
        )
    }
}
