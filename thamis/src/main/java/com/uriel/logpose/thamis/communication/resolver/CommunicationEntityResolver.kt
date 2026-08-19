package com.uriel.logpose.thamis.communication.resolver

import com.uriel.logpose.thamis.communication.model.ContactCandidate
import com.uriel.logpose.thamis.communication.model.ContactResolution
import java.util.*

/**
 * Resuelve identidades (contactos, grupos, apodos) en el dominio de comunicación.
 */
object CommunicationEntityResolver {

    private val mockDatabase = listOf(
        ContactCandidate("1", "Juan Perez", "+541122334455", alias = listOf("Juan")),
        ContactCandidate("2", "Juan Trabajo", "+541199887766", alias = listOf("Juan")),
        ContactCandidate("3", "Mama", "+541155443322", alias = listOf("Mamá", "Vieja")),
        ContactCandidate("4", "Riders", "group_id_123", alias = listOf("Grupo Riders"), priority = 10)
    )

    fun resolve(entityName: String?): ContactResolution {
        if (entityName.isNullOrBlank()) return ContactResolution("", emptyList())

        val query = entityName.lowercase(Locale.getDefault()).trim()
        val candidates = mockDatabase.filter { 
            it.name.lowercase().contains(query) || 
            it.alias.any { a -> a.lowercase().contains(query) }
        }

        return when {
            candidates.isEmpty() -> ContactResolution(entityName, emptyList())
            candidates.size == 1 -> ContactResolution(entityName, candidates, candidates.first())
            else -> ContactResolution(entityName, candidates, isAmbiguous = true)
        }
    }
}
