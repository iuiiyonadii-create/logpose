package com.uriel.logpose.core.nlp

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object LanguageLoader {

    fun loadObject(

        context: Context,

        fileName: String

    ): JSONObject {

        return try {

            val json = context.assets
                .open(fileName)
                .bufferedReader()
                .use { it.readText() }

            JSONObject(json)

        } catch (_: Exception) {

            JSONObject()
        }
    }

    fun loadArray(

        context: Context,

        fileName: String

    ): JSONArray {

        return try {

            val json = context.assets
                .open(fileName)
                .bufferedReader()
                .use { it.readText() }

            JSONArray(json)

        } catch (_: Exception) {

            JSONArray()
        }
    }
}