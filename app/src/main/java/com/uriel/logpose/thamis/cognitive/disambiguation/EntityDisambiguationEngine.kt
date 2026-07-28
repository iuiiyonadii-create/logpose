package com.uriel.logpose.thamis.cognitive.disambiguation

import com.uriel.logpose.thamis.cognitive.model.WorldState
import com.uriel.logpose.thamis.cognitive.utils.ThamisLogProvider

/**
 * Motor de Desambiguación de Entidades v1.0.
 * Resuelve conflictos cuando un término puede ser múltiples tipos de entidad.
 */
object EntityDisambiguationEngine {

    private const val TAG = "THAMIS_DISAMBIGUATION"

    fun disambiguate(entityName: String, worldState: WorldState): EntityDecision {
        val candidates = mutableListOf<EntityCandidate>()
        val evidences = mutableListOf<DisambiguationEvidence>()

        ThamisLogProvider.logger?.d(TAG, "INPUT: $entityName")

        // 1. Simular generación de candidatos (Esto vendrá de bases de datos en v1.1)
        // Ejemplo Rockstar y Maps
        if (entityName.lowercase().contains("rock")) {
            candidates.add(EntityCandidate("Rockstar", EntityCandidate.Type.SONG, 0.7f, "Phonetic"))
            candidates.add(EntityCandidate("Rockstar", EntityCandidate.Type.APP, 0.3f, "System"))
        }

        if (entityName.lowercase().contains("maps")) {
            candidates.add(EntityCandidate("Maps", EntityCandidate.Type.PLACE, 0.6f, "NLP"))
            candidates.add(EntityCandidate("Maps", EntityCandidate.Type.APP, 0.5f, "System"))
        }

        // 2. Evaluar evidencias
        
        // Evidencia: Música sonando
        if (worldState.system.isMusicPlaying) {
            evidences.add(DisambiguationEvidence(
                DisambiguationEvidence.Type.MUSIC_PLAYING,
                0.4f,
                "Spotify activo favorece música"
            ))
        }

        // Evidencia: Navegación activa o verbo explícito (Simulado)
        if (worldState.driving.hasActiveNavigation || entityName.contains("abrí")) {
            evidences.add(DisambiguationEvidence(
                DisambiguationEvidence.Type.RECENT_ACTION,
                0.5f,
                "Acción explícita o estado de navegación"
            ))
        }

        // Evidencia: App instalada (Placeholder)
        evidences.add(DisambiguationEvidence(
            DisambiguationEvidence.Type.APP_INSTALLED,
            0.1f,
            "App encontrada en sistema"
        ))

        // 3. Calcular puntajes finales
        val results = candidates.map { candidate ->
            var finalScore = candidate.confidence
            evidences.forEach { evidence ->
                // Aplicar impacto según compatibilidad
                if (candidate.entityType == EntityCandidate.Type.SONG && evidence.type == DisambiguationEvidence.Type.MUSIC_PLAYING) {
                    finalScore += evidence.impact
                }
                if (candidate.entityType == EntityCandidate.Type.APP && evidence.type == DisambiguationEvidence.Type.APP_INSTALLED) {
                    finalScore += evidence.impact
                }
            }
            candidate.copy(confidence = finalScore.coerceIn(0f, 1f))
        }

        val winner = results.maxByOrNull { it.confidence }
        
        ThamisLogProvider.logger?.d(TAG, "CANDIDATES:")
        results.forEach { ThamisLogProvider.logger?.d(TAG, "   ${it.entityType}: ${it.confidence}") }
        ThamisLogProvider.logger?.d(TAG, "WINNER: ${winner?.entityType}")

        return EntityDecision(
            selectedEntity = winner,
            confidence = winner?.confidence ?: 0f,
            rejectedCandidates = results.filter { it != winner },
            evidences = evidences,
            reasoning = "Prioridad por contexto y estado del sistema"
        )
    }
}
