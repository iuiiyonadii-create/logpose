package com.uriel.logpose.thamis.knowledge.future

import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.knowledge.KnowledgeRule

/**
 * Registro de visión a largo plazo para el ecosistema LogPose.
 * Ideas "sembradas" para que el sistema evolucione hacia IoT y Smart Home.
 */
object FutureVision {

    val roadmap = mapOf(
        "DOMOTICA" to "Integración con Smart TVs para control de volumen, canales y encendido vía comandos de voz.",
        "ECOSISTEMA_EXTENDIDO" to "Control de dispositivos IoT fuera del smartphone (luces, portones, etc).",
        "CONTEXTO_HOGAR" to "Detección de llegada a casa para transferencia de sesión PC -> TV."
    )

    val rules = listOf(
        KnowledgeRule(
            intent = Intent.SMART_TV_CONTROL,
            phrases = setOf(
                "prender tele", "apagar tele", "cambiar canal", 
                "smart tv", "conectar con tv", "subir volumen tele"
            )
        ),
        KnowledgeRule(
            intent = Intent.IOT_CONTROL,
            phrases = setOf(
                "prender luz", "apagar luz", "abrir porton", "cerrar porton",
                "domotica", "casa inteligente"
            )
        )
    )
}
