package com.uriel.logpose.thamis.field_test.monitor

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.field_test.model.FieldEvent
import com.uriel.logpose.thamis.field_test.model.TestSession

/**
 * Gestor principal de las pruebas de campo en conducción real.
 */
object FieldTestManager {
    private var activeSession: TestSession? = null
    private val sessions = mutableListOf<TestSession>()

    fun startSession(testerName: String, device: String, scenario: String) {
        activeSession = TestSession(
            testerName = testerName,
            deviceModel = device,
            appVersion = "v1.0.0-beta",
            scenario = scenario,
            conditions = emptyMap()
        )
        LogPoseLogger.i("THAMIS_FIELD: Iniciando sesión de prueba de campo para $testerName")
    }

    fun recordEvent(type: String, description: String, severity: String = "INFO") {
        activeSession?.events?.add(FieldEvent(type = type, description = description, severity = severity))
    }

    fun endSession(): TestSession? {
        val session = activeSession
        session?.let {
            it.endTime = System.currentTimeMillis()
            sessions.add(it)
            LogPoseLogger.i("THAMIS_FIELD: Sesión ${it.id} finalizada. Kilómetros y eventos registrados.")
        }
        activeSession = null
        return session
    }
}
