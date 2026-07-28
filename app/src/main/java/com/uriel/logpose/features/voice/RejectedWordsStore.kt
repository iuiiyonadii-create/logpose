package com.uriel.logpose.features.voice

import android.content.Context

/**
 * Persiste las palabras que el modelo acústico rechaza para no re-testearlas.
 */
class RejectedWordsStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun save(modelVersion: String, rejected: List<String>) {
        prefs.edit()
            .putString(KEY_MODEL_VERSION, modelVersion)
            .putStringSet(KEY_REJECTED_WORDS, rejected.toSet())
            .apply()
    }

    fun load(currentModelVersion: String): List<String>? {
        val savedVersion = prefs.getString(KEY_MODEL_VERSION, null)
        if (savedVersion != currentModelVersion) return null
        return prefs.getStringSet(KEY_REJECTED_WORDS, null)?.toList()
    }

    companion object {
        private const val PREFS_NAME = "logpose_grammar_cache"
        private const val KEY_MODEL_VERSION = "model_version"
        private const val KEY_REJECTED_WORDS = "rejected_words"
    }
}
