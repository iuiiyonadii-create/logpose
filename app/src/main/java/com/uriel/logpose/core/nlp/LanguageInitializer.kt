package com.uriel.logpose.core.nlp

import android.content.Context

object LanguageInitializer {

    private var initialized = false

    suspend fun initialize(
        context: Context
    ) {
        if (initialized) {
            return
        }

        LanguageRepository.initialize(context)

        initialized = true
    }

    fun initialized(): Boolean =
        initialized
}