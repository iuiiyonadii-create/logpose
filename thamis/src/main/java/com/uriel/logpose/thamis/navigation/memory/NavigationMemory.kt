package com.uriel.logpose.thamis.navigation.memory

/**
 * Memoria central de navegación de THAMIS.
 * Permite que el sistema aprenda hábitos y mejore la confianza basada en el pasado.
 */
object NavigationMemory {
    
    private val history = NavigationHistory()

    fun recordExperience(experience: NavigationExperience) {
        history.addExperience(experience)
    }

    fun getMemoryBoost(destination: String): Float {
        val successRate = history.getSuccessRate(destination)
        // Bonus de confianza de hasta +0.2 si el destino es habitual y exitoso
        return (successRate * 0.2f)
    }

    fun getFrequentDestination(): String? = history.getMostFrequentDestination()
}
