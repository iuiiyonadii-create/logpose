package com.uriel.logpose.thamis.navigation

import com.uriel.logpose.thamis.navigation.model.NavigationGoal
import com.uriel.logpose.thamis.language.SimilarityEngine

/**
 * Resuelve términos genéricos ("casa", "trabajo") en destinos concretos.
 * Optimizado para modismos argentinos y variaciones de voz.
 */
object DestinationResolver {

    private val HOME_SYNONYMS = listOf("casa", "mi casa", "mi rancho", "la choza", "vuelvo")
    private val WORK_SYNONYMS = listOf("trabajo", "laburo", "oficina", "empresa", "el kiosco")

    fun resolve(text: String): NavigationGoal.GoalType {
        val normalized = text.lowercase().trim()
        
        // 1. Similitud con "Casa"
        if (HOME_SYNONYMS.any { SimilarityEngine.score(normalized, it) > 0.85f }) {
            return NavigationGoal.GoalType.GO_HOME
        }

        // 2. Similitud con "Trabajo"
        if (WORK_SYNONYMS.any { SimilarityEngine.score(normalized, it) > 0.85f }) {
            return NavigationGoal.GoalType.GO_WORK
        }

        // 3. Puntos de Interés comunes
        if (normalized.contains("mcdonald") || normalized.contains("burguer") || 
            normalized.contains("estacion") || normalized.contains("estación")) {
            return NavigationGoal.GoalType.GO_POI
        }

        // 4. Contactos (Simulado)
        if (normalized.contains("juan") || normalized.contains("pedro") || 
            normalized.contains("maria") || normalized.contains("mamá")) {
            return NavigationGoal.GoalType.GO_CONTACT
        }

        // Default: Dirección literal
        return NavigationGoal.GoalType.GO_ADDRESS
    }
}
