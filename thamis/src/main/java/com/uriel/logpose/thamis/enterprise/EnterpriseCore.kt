package com.uriel.logpose.thamis.enterprise

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * FASE 25.22 — THAMIS ENTERPRISE ARCHITECTURE
 * FASE 1: ENTERPRISE CORE
 */
object EnterpriseCore {

    private var currentOrgId: String? = null

    fun initializeCompany(orgId: String) {
        currentOrgId = orgId
        LogPoseLogger.i("EnterpriseCore: Organización inicializada: $orgId")
    }

    fun isEnterpriseEnabled(): Boolean = currentOrgId != null
}
