package com.uriel.logpose.thamis.communication.resolver

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.communication.model.ContactCandidate
import com.uriel.logpose.thamis.communication.model.ContactResolution
import com.uriel.logpose.thamis.language.SimilarityEngine
import com.uriel.logpose.thamis.language.advanced.AdvancedLanguageEngine
import com.uriel.logpose.thamis.learning.LearningEngine
import java.util.*

/**
 * ContactResolver V2: Motor de inteligencia para resolución de contactos.
 * Utiliza ranking híbrido: Similitud Fonética + Historial + Favoritos.
 */
object ContactResolver {
    
    private val contactDatabase = mutableListOf<ContactCandidate>()

    /**
     * Resuelve un nombre hablado a un contacto real usando el ranking engine.
     */
    fun resolve(spokenName: String): ContactResolution {
        val query = spokenName.lowercase(Locale.getDefault()).trim()
        if (query.isEmpty()) return ContactResolution(spokenName, emptyList())

        // 1. Verificamos Memoria Personal (Alias aprendidos)
        val learnedIntent = LearningEngine.getLearnedIntent("llamar a $query")
        // TODO: En el futuro el LearningEngine podría devolver entidades resueltas directamente.
        
        LogPoseLogger.d("ContactResolver: Buscando match para '$query' en ${contactDatabase.size} contactos...")

        // 2. Ranking de candidatos
        val candidates = contactDatabase.map { contact ->
            val score = calculateScore(query, contact)
            contact.copy().apply { this.score = score }
        }.filter { it.score > 0.4f } // Umbral de corte
        .sortedByDescending { it.score }

        return when {
            candidates.isEmpty() -> {
                LogPoseLogger.w("ContactResolver: No se encontraron candidatos para '$spokenName'")
                ContactResolution(spokenName, emptyList())
            }
            candidates.size == 1 || (candidates[0].score > 0.9f && candidates[0].score > candidates.getOrNull(1)?.score?.plus(0.3f) ?: 0f) -> {
                val winner = candidates.first()
                LogPoseLogger.i("ContactResolver: Ganador indiscutido: ${winner.name} (Score: ${winner.score})")
                ContactResolution(spokenName, candidates, winner)
            }
            else -> {
                LogPoseLogger.i("ContactResolver: Ambigüedad detectada entre ${candidates.size} contactos.")
                ContactResolution(spokenName, candidates, isAmbiguous = true)
            }
        }
    }

    private fun calculateScore(query: String, contact: ContactCandidate): Float {
        // A. Match Exacto (Nombre o Alias)
        if (contact.name.equals(query, ignoreCase = true)) return 1.0f
        if (contact.alias.any { it.equals(query, ignoreCase = true) }) return 1.0f

        // B. Similitud Avanzada (Fonética + Estructural)
        val nameScore = AdvancedLanguageEngine.getSimilarity(query, contact.name)
        
        var aliasMaxScore = 0f
        for (alias in contact.alias) {
            val s = AdvancedLanguageEngine.getSimilarity(query, alias)
            if (s > aliasMaxScore) aliasMaxScore = s
        }
        
        val baseScore = maxOf(nameScore, aliasMaxScore)

        // C. Boost por Favorito
        var finalScore = baseScore
        if (contact.isFavorite) finalScore += 0.1f

        // D. Boost por Frecuencia de uso (Learning Engine integration)
        // Simulamos un boost basado en el uso previo
        if (contact.callFrequency > 5) finalScore += 0.05f
        
        return finalScore.coerceIn(0.0f, 1.0f)
    }

    fun populate(contacts: List<ContactCandidate>) {
        contactDatabase.clear()
        contactDatabase.addAll(contacts)
        LogPoseLogger.i("ContactResolver: Base de datos poblada con ${contacts.size} contactos.")
    }

    fun addContact(contact: ContactCandidate) {
        contactDatabase.add(contact)
    }

    fun clear() {
        contactDatabase.clear()
    }

    fun getAllNames(): List<String> = contactDatabase.map { it.name }
}
