package com.uriel.logpose.core.speech

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.parser.PhoneticDictionary

/**
 * HotwordsCollector (Fase 1): Compila dinámicamente la lista de hotwords (Contextual Biasing)
 * para Sherpa-ONNX desde 3 fuentes del proyecto:
 * 1. Música, Artistas y Playlists (Spotify / LearningEngine)
 * 2. Agenda de Contactos del Dispositivo (ContactsContract)
 * 3. POIs / Destinos frecuentes (Glosario Navegación)
 */
object HotwordsCollector {

    data class CollectionResult(
        val musicCount: Int,
        val contactsCount: Int,
        val poiCount: Int,
        val buffer: String
    )

    fun buildHotwordsBuffer(context: Context = LogPoseApplication.instance): String {
        return collectHotwords(context).buffer
    }

    fun collectHotwords(context: Context = LogPoseApplication.instance): CollectionResult {
        val hotwordsSet = LinkedHashSet<String>()

        // 1. FUENTE 1: Música, Artistas y Playlists (Spotify / LearningEngine)
        val musicEntries = mutableListOf<String>()
        try {
            val dictionary = PhoneticDictionary(context)
            musicEntries.addAll(dictionary.listaDe("musica.artistas"))
            musicEntries.addAll(dictionary.listaDe("musica.canciones"))
            musicEntries.addAll(dictionary.listaDe("musica.playlists"))
            musicEntries.addAll(com.uriel.logpose.thamis.learning.LearningEngine.getLearnedMusicEntities())
        } catch (e: Exception) {
            LogPoseLogger.e("HotwordsCollector: Error leyendo fuente de Música: ${e.message}")
        }
        val musicClean = musicEntries
            .map { cleanHotword(it) }
            .filter { it.isNotBlank() && it.length >= 3 }
            .distinct()
        hotwordsSet.addAll(musicClean)
        val musicCount = musicClean.size

        // 2. FUENTE 2: Agenda de Contactos del Dispositivo (ContactsContract)
        val contactNames = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            try {
                val cursor = context.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME),
                    null,
                    null,
                    "${ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED} DESC LIMIT 100"
                )
                cursor?.use {
                    val nameIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    while (it.moveToNext()) {
                        val name = if (nameIdx >= 0) it.getString(nameIdx) else ""
                        if (!name.isNullOrBlank()) {
                            contactNames.add(name)
                        }
                    }
                }
            } catch (e: Exception) {
                LogPoseLogger.e("HotwordsCollector: Error leyendo Contactos: ${e.message}")
            }
        } else {
            LogPoseLogger.w("HotwordsCollector: Permiso READ_CONTACTS no otorgado. Omitiendo agenda de contactos.")
        }
        val contactsClean = contactNames
            .map { cleanHotword(it) }
            .filter { it.isNotBlank() && it.length >= 2 }
            .distinct()
        hotwordsSet.addAll(contactsClean)
        val contactsCount = contactsClean.size

        // 3. FUENTE 3: POIs / Destinos Frecuentes (Glosario Navegación)
        val poiEntries = mutableListOf<String>()
        try {
            val dictionary = PhoneticDictionary(context)
            poiEntries.addAll(dictionary.listaDe("navegacion.destinos_comunes"))
            poiEntries.addAll(dictionary.listaDe("navegacion.favoritos"))
        } catch (e: Exception) {
            LogPoseLogger.e("HotwordsCollector: Error leyendo fuente de POI: ${e.message}")
        }
        val poiClean = poiEntries
            .map { cleanHotword(it) }
            .filter { it.isNotBlank() && it.length >= 3 }
            .distinct()
        hotwordsSet.addAll(poiClean)
        val poiCount = poiClean.size

        val bufferString = hotwordsSet.joinToString("\n")
        LogPoseLogger.i("HOTWORDS: Buffer compilado -> Música: $musicCount | Contactos: $contactsCount | POIs: $poiCount | Total único: ${hotwordsSet.size}")

        return CollectionResult(musicCount, contactsCount, poiCount, bufferString)
    }

    private fun cleanHotword(input: String): String {
        return input.lowercase().trim()
            .replace(Regex("[áàäâã]"), "a")
            .replace(Regex("[éèëê]"), "e")
            .replace(Regex("[íìïî]"), "i")
            .replace(Regex("[óòöôõ]"), "o")
            .replace(Regex("[úùüû]"), "u")
            .replace(Regex("[^a-z0-9 ]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}
