package com.uriel.logpose.core.nlp

data class NlpResult(

    val intent: Intent,

    val entities: List<Entity>,

    val slots: List<Slot>,

    val confidence: Float
)