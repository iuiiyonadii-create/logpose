package com.uriel.logpose.thamis.knowledge

import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.parser.PhoneticDictionary
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.learning.LearningEngine
import com.thamis.lab.core.contracts.intent.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            
            // Ingesta dinámica de modismos y alucinaciones corregidas del glosario
            val modismosMap = dictionary.mapaDe("modismos")
            val volumeModismos = modismosMap.filterValues { it.contains("volumen") || it.contains("máximo") || it.contains("fuerte") }.keys
            if (volumeModismos.isNotEmpty()) {
                add(KnowledgeRule(Intent.SET_VOLUME, volumeModismos.toSet()))
            }
            
            val navModismos = modismosMap.filterValues { it.contains("ir") || it.contains("navegar") || it.contains("casa") || it.contains("trabajo") }.keys
            if (navModismos.isNotEmpty()) {
                add(KnowledgeRule(Intent.NAVIGATE, navModismos.toSet()))
            }

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
