package com.uriel.logpose.thamis.global

/**
 * FASE 25.23 — THAMIS GLOBAL SCALE PREPARATION
 * FASE 2: LANGUAGE SYSTEM
 */
object LanguageManager {
    
    private var currentLanguage: String = "es"

    fun setLanguage(lang: String) {
        currentLanguage = lang
    }

    fun getLanguage(): String = currentLanguage

    fun getSupportedLanguages(): List<String> = listOf("es", "en", "pt")
}
