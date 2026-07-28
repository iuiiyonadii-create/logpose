package com.uriel.logpose.thamis_ai.global

import java.util.Locale

/**
 * Manages UI and voice language settings (ES, EN, PT).
 */
class LanguageManager {
    private var currentLocale = Locale("es")

    fun setLanguage(localeCode: String) {
        currentLocale = Locale(localeCode)
    }

    fun getLanguage() = currentLocale.language
}
