package com.uriel.logpose.thamis.request

/**
 * Entrada principal de THAMIS.
 */
data class THAMISRequest(

    val text: String,

    val speechConfidence: Float = 1f

)