package com.uriel.logpose.core.compat.core

import android.content.Context

object AppContextProvider {
    var appContext: Context? = null
    
    val applicationContext: Context
        get() = appContext ?: throw IllegalStateException("AppContextProvider not initialized")
}
