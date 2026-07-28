package com.uriel.logpose.thamis.communication.resolver

import com.uriel.logpose.thamis.communication.model.ContactCandidate
import com.uriel.logpose.thamis.communication.model.ContactResolution
import java.util.*

/**
 * ContactResolver: Encargado de mapear nombres hablados a identidades reales.
 */
object ContactResolver {
    
    // Base de datos simulada (Pure Kotlin)
    private val contactDatabase = mutableListOf<ContactCandidate>()

    fun resolve(name: String): ContactResolution {
        val query = name.lowercase(Locale.getDefault()).trim()
        
        val matches = contactDatabase.filter { 
            it.name.lowercase().contains(query) || 
            it.alias.any { a -> a.lowercase().contains(query) }
        }

        return when {
            matches.isEmpty() -> ContactResolution(name, emptyList())
            matches.size == 1 -> ContactResolution(name, matches, matches.first())
            else -> {
                // Si hay favoritos, priorizamos
                val favorites = matches.filter { it.isFavorite }
                if (favorites.size == 1) {
                    ContactResolution(name, matches, favorites.first())
                } else {
                    ContactResolution(name, matches, isAmbiguous = true)
                }
            }
        }
    }

    fun addContact(contact: ContactCandidate) {
        contactDatabase.add(contact)
    }

    fun clear() {
        contactDatabase.clear()
    }
}
