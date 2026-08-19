package com.uriel.logpose.features.voice

import android.content.ComponentName
import com.uriel.logpose.features.settings.SettingsManager


/**
 * Configuración de voz leída desde SettingsManager.
 * Centraliza idioma, modelo de lenguaje y motor seleccionado.
 */
data class VoiceConfiguration(
    val language: String = DEFAULT_LANGUAGE,
    val languageModel: String = DEFAULT_LANGUAGE_MODEL,
    val enginePackage: String? = null,
    val engineClass: String? = null,
    val partialResults: Boolean = true,
    val maxResults: Int = 5
) {


    /**
     * ComponentName del motor de voz seleccionado.
     * null = usa el motor predeterminado del sistema.
     */
    val engineComponent: ComponentName?
        get() {
            val pkg = enginePackage ?: return null
            val cls = engineClass ?: return null
            return ComponentName(pkg, cls)
        }


    companion object {

        const val DEFAULT_LANGUAGE = "es-AR"
        const val DEFAULT_LANGUAGE_MODEL = "free_form"

        // Claves de SettingsManager
        private const val KEY_VOICE_LANGUAGE = "voice_language"
        private const val KEY_VOICE_MODEL = "voice_language_model"
        private const val KEY_VOICE_ENGINE_PACKAGE = "voice_engine_package"
        private const val KEY_VOICE_ENGINE_CLASS = "voice_engine_class"
        private const val KEY_VOICE_PARTIAL_RESULTS = "voice_partial_results"
        private const val KEY_VOICE_MAX_RESULTS = "voice_max_results"


        /**
         * Lee la configuración actual desde SettingsManager.
         */
        fun from(settings: SettingsManager): VoiceConfiguration {
            return VoiceConfiguration(
                language = settings.getString(
                    KEY_VOICE_LANGUAGE, DEFAULT_LANGUAGE
                ) ?: DEFAULT_LANGUAGE,

                languageModel = settings.getString(
                    KEY_VOICE_MODEL, DEFAULT_LANGUAGE_MODEL
                ) ?: DEFAULT_LANGUAGE_MODEL,

                enginePackage = settings.getString(
                    KEY_VOICE_ENGINE_PACKAGE
                ),

                engineClass = settings.getString(
                    KEY_VOICE_ENGINE_CLASS
                ),

                partialResults = settings.getBoolean(
                    KEY_VOICE_PARTIAL_RESULTS, true
                ),

                maxResults = settings.getInt(
                    KEY_VOICE_MAX_RESULTS, 5
                )
            )
        }
    }
}
