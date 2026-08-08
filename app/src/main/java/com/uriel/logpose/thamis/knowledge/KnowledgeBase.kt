package com.uriel.logpose.thamis.knowledge

import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.parser.PhoneticDictionary
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.knowledge.apps.AppKnowledge
import com.uriel.logpose.thamis.knowledge.apps.NotificationKnowledge
import com.uriel.logpose.thamis.knowledge.calls.CallKnowledge
import com.uriel.logpose.thamis.knowledge.calls.HandoverKnowledge
import com.uriel.logpose.thamis.knowledge.legal.LegalKnowledge
import com.uriel.logpose.thamis.knowledge.emergency.EmergencyKnowledge
import com.uriel.logpose.thamis.knowledge.future.FutureVision
import com.uriel.logpose.thamis.knowledge.maps.NavigationKnowledge
import com.uriel.logpose.thamis.knowledge.music.MusicKnowledge
import com.uriel.logpose.thamis.knowledge.music.VolumeKnowledge
import com.uriel.logpose.thamis.knowledge.regional.RegionalKnowledge
import com.uriel.logpose.thamis.knowledge.weather.WeatherKnowledge

/**
 * Reúne todo el conocimiento disponible de THAMIS.
 * Mejorado (Misión #011): Soporte para búsqueda indexada (Fast Search).
 */
object KnowledgeBase {

    private val dictionary: PhoneticDictionary by lazy {
        PhoneticDictionary(LogPoseApplication.instance)
    }

    fun resolveAppName(raw: String): String? = dictionary.resolverApp(raw)

    val entries: List<KnowledgeRule> by lazy {
        buildList {
            addAll(NavigationKnowledge.rules)
            addAll(NotificationKnowledge.rules)
            addAll(MusicKnowledge.rules)
            addAll(VolumeKnowledge.rules)
            addAll(CallKnowledge.rules)
            addAll(HandoverKnowledge.rules)
            addAll(WeatherKnowledge.rules)
            addAll(EmergencyKnowledge.rules)
            addAll(AppKnowledge.rules)
            addAll(LegalKnowledge.rules)
            addAll(FutureVision.rules)
            addAll(RegionalKnowledge.rules)
            add(KnowledgeRule(Intent.OPEN_APP, setOf("logpose privacidad", "logpose volver", "logpose estado", "logpose apagar", "logpose desactivar")))
        }
    }

    /**
     * Índice Invertido: Mapea una llave fonética a un conjunto de reglas candidatas.
     * Permite pasar de O(N) a O(1) en la fase de filtrado inicial.
     */
    val phoneticIndex: Map<String, List<KnowledgeRule>> by lazy {
        val index = mutableMapOf<String, MutableList<KnowledgeRule>>()
        entries.forEach { rule ->
            rule.phoneticKeys.values.forEach { meta ->
                index.getOrPut(meta) { mutableListOf() }.add(rule)
            }
        }
        index
    }
}
