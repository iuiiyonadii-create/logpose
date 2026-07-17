package com.uriel.logpose.core.nlp

data class Language(

    val aliases: Map<String, String> = emptyMap(),

    val entities: Map<String, List<String>> = emptyMap(),

    val intents: Map<String, List<String>> = emptyMap(),

    val phrases: Map<String, List<String>> = emptyMap(),

    val separators: List<String> = emptyList(),

    val stopWords: List<String> = emptyList()
)