package com.uriel.logpose.thamis.navigation.memory

/**
 * Contenedor para el historial de experiencias de navegación.
 */
class NavigationHistory {
    private val experiences = mutableListOf<NavigationExperience>()

    fun addExperience(experience: NavigationExperience) {
        experiences.add(experience)
    }

    fun getAll(): List<NavigationExperience> = experiences.toList()

    fun getMostFrequentDestination(): String? {
        return experiences.groupBy { it.destination }
            .maxByOrNull { it.value.size }?.key
    }
    
    fun getSuccessRate(destination: String): Float {
        val destExperiences = experiences.filter { it.destination == destination }
        if (destExperiences.isEmpty()) return 0f
        return destExperiences.count { it.success }.toFloat() / destExperiences.size
    }
}
