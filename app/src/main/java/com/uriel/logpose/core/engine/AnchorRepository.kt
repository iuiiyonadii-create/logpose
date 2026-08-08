package com.uriel.logpose.core.engine

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AnchorDomain { MUSIC_ARTIST, MUSIC_TRACK, CONTACT, APP_COMMAND, NAVIGATION }

data class AnchorToken(
    val rawText: String,
    val normalizedPhonetic: String,
    val domain: AnchorDomain,
    val weight: Float = 1.0f
)

/**
 * AnchorRepository v4.5: Almacén de entidades con acceso O(1).
 * Implementa seguridad de hilos extrema para operación Always-On.
 */
class AnchorRepository {
    private val activeAnchors = AtomicReference<Map<String, AnchorToken>>(emptyMap())
    private val _glosaryState = MutableStateFlow<Set<AnchorToken>>(emptySet())
    val glosaryState: StateFlow<Set<AnchorToken>> = _glosaryState.asStateFlow()

    private val musicAnchors = ConcurrentHashMap<String, AnchorToken>()
    private val contactAnchors = ConcurrentHashMap<String, AnchorToken>()
    private val navigationAnchors = ConcurrentHashMap<String, AnchorToken>()

    fun updateMusicEntities(artistsAndTracks: List<Pair<String, AnchorDomain>>) {
        musicAnchors.clear()
        artistsAndTracks.forEach { (name, domain) ->
            val cleanKey = name.lowercase().trim()
            musicAnchors[cleanKey] = AnchorToken(
                rawText = name,
                normalizedPhonetic = cleanKey,
                domain = domain,
                weight = 1.2f
            )
        }
        rebuildActiveMap()
    }

    fun updateNavigationEntities(destinations: List<String>) {
        navigationAnchors.clear()
        destinations.forEach { name ->
            val cleanKey = name.lowercase().trim()
            navigationAnchors[cleanKey] = AnchorToken(
                rawText = name,
                normalizedPhonetic = cleanKey,
                domain = AnchorDomain.NAVIGATION,
                weight = 1.1f
            )
        }
        rebuildActiveMap()
    }

    fun updateContacts(contactNames: List<String>) {
        contactAnchors.clear()
        contactNames.forEach { name ->
            val cleanKey = name.lowercase().trim()
            contactAnchors[cleanKey] = AnchorToken(
                rawText = name,
                normalizedPhonetic = cleanKey,
                domain = AnchorDomain.CONTACT,
                weight = 1.0f
            )
        }
        rebuildActiveMap()
    }

    private fun rebuildActiveMap() {
        val combined = HashMap<String, AnchorToken>()
        combined.putAll(contactAnchors)
        combined.putAll(musicAnchors)
        combined.putAll(navigationAnchors)
        activeAnchors.set(combined)
        _glosaryState.value = combined.values.toSet()
    }

    fun findAnchor(token: String): AnchorToken? {
        return activeAnchors.get()[token.lowercase().trim()]
    }
    
    fun getAllNames(): List<String> = activeAnchors.get().keys.toList()
}
