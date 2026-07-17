package com.uriel.logpose.core.nlp

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object LanguageRepository {

    private lateinit var aliases: JSONObject
    private lateinit var entities: JSONObject
    private lateinit var intents: JSONObject
    private lateinit var phrases: JSONObject

    private lateinit var separators: JSONArray
    private lateinit var stopWords: JSONArray

    fun initialize(

        context: Context

    ) {

        aliases =
            LanguageLoader.loadObject(
                context,
                "language/es/aliases.json"
            )

        entities =
            LanguageLoader.loadObject(
                context,
                "language/es/entities.json"
            )

        intents =
            LanguageLoader.loadObject(
                context,
                "language/es/intents.json"
            )

        phrases =
            LanguageLoader.loadObject(
                context,
                "language/es/phrases.json"
            )

        separators =
            LanguageLoader.loadArray(
                context,
                "language/es/separators.json"
            )

        stopWords =
            LanguageLoader.loadArray(
                context,
                "language/es/stopwords.json"
            )
    }

    fun aliases() = aliases

    fun entities() = entities

    fun intents() = intents

    fun phrases() = phrases

    fun separators() = separators

    fun stopWords() = stopWords
}