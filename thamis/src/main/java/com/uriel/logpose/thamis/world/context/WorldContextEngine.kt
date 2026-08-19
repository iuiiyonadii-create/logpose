package com.uriel.logpose.thamis.world.context

import com.uriel.logpose.thamis.world.engine.WorldModelEngine
import com.uriel.logpose.thamis.world.model.WorldSnapshot

/**
 * Capa de compatibilidad que delega al nuevo WorldModelEngine.
 */
object WorldContextEngine {
    fun getCurrentState(): WorldSnapshot = WorldModelEngine.getCurrentSnapshot()
}
