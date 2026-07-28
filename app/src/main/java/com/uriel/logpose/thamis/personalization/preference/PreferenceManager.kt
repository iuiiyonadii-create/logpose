package com.uriel.logpose.thamis.personalization.preference

import com.uriel.logpose.thamis.personalization.model.PreferenceType
import com.uriel.logpose.thamis.personalization.model.UserPreference

/**
 * Administra el almacén de preferencias cognitivas del usuario.
 */
object PreferenceManager {
    private val preferences = mutableMapOf<PreferenceType, UserPreference>()

    fun setPreference(type: PreferenceType, value: String, confidence: Float) {
        preferences[type] = UserPreference(type, value, confidence)
    }

    fun getPreference(type: PreferenceType): UserPreference? = preferences[type]

    fun getAllPreferences(): List<UserPreference> = preferences.values.toList()
}
