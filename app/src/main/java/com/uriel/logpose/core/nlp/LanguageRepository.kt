package com.uriel.logpose.core.nlp

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

object LanguageRepository {

    private lateinit var aliases: JSONObject
    private lateinit var entities: JSONObject
    private lateinit var intents: JSONObject
    private lateinit var phrases: JSONObject

    private lateinit var separators: JSONArray
    private lateinit var stopWords: JSONArray

    private var isInitialized = false

    suspend fun initialize(context: Context) = withContext(Dispatchers.IO) {
        if (isInitialized) return@withContext
        
        try {
            val jsonString = context.assets.open("logpose_glosario.json").bufferedReader().use { it.readText() }
            val root = JSONObject(jsonString)

            // Mapeo Staff-Level: Adaptamos el glosario unificado a la estructura legacy para no romper nada
            val tempAliases = JSONObject()
            val verbos = root.getJSONObject("verbos")
            verbos.keys().forEach { key ->
                val arr = verbos.getJSONArray(key)
                for (i in 0 until arr.length()) {
                    tempAliases.put(arr.getString(i), key)
                }
            }
            aliases = tempAliases

            entities = JSONObject()
            entities.put("apps", root.getJSONObject("apps"))
            entities.put("artistas", root.getJSONObject("musica").getJSONArray("artistas"))

            intents = root.getJSONObject("verbos")
            phrases = root.getJSONObject("verbos") // Usado por NlpEngine

            separators = JSONArray().apply { put(" y "); put(",") }
            stopWords = root.getJSONArray("muletillas_a_ignorar")
            
            isInitialized = true
        } catch (e: Exception) {
            com.uriel.logpose.core.compat.core.LogPoseLogger.e("LanguageRepository: Error cargando glosario: ${e.message}")
        }
    }

    fun aliases() = aliases

    fun entities() = entities

    fun intents() = intents

    fun phrases() = phrases

    fun separators() = separators

    fun stopWords() = stopWords
    
    fun isReady() = isInitialized
}