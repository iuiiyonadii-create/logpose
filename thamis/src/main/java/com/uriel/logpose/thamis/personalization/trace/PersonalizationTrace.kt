package com.uriel.logpose.thamis.personalization.trace

import com.uriel.logpose.thamis.personalization.model.PreferenceType

/**
 * Registro de cambios en el perfil del usuario.
 */
data class PersonalizationTrace(
    val userId: String,
    val preference: PreferenceType,
    val oldValue: String?,
    val newValue: String,
    val reason: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)

object PersonalizationAudit {
    private val logs = mutableListOf<PersonalizationTrace>()

    fun record(trace: PersonalizationTrace) {
        logs.add(trace)
        if (logs.size > 200) logs.removeAt(0)
    }

    fun getLogs(): List<PersonalizationTrace> = logs.toList()
}
