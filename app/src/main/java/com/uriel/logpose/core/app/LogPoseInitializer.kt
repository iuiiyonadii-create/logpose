package com.uriel.logpose.core.app

import android.content.Context
import androidx.startup.Initializer
import com.uriel.logpose.core.compat.core.LogPoseLogger

class LogPoseInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        LogPoseLogger.initialize(context)
        LogPoseLogger.i("LogPoseInitializer: Iniciando secuencia de carga...")
        // AppLauncher y otros se inicializan vía Hilt/Lazy
        com.uriel.logpose.core.services.AlertManager.initialize(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
