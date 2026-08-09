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

    /**
     * Carga e hidrata la Semilla Staff (staff_seed.json) automáticamente al iniciar.
     */
    fun initializeStaffSeed(context: android.content.Context) {
        try {
            val jsonString = context.assets.open("staff_seed.json").bufferedReader().use { it.readText() }
            val seed = org.json.JSONObject(jsonString)

            if (seed.has("urban_matrix")) {
                val urbanArray = seed.getJSONArray("urban_matrix")
                for (i in 0 until urbanArray.length()) {
                    val item = urbanArray.getJSONObject(i)
                    val name = item.getString("name")
                    com.uriel.logpose.thamis.learning.LearningEngine.learn(name, name, Intent.NAVIGATE)
                }
            }

            if (seed.has("music_dna")) {
                val musicArray = seed.getJSONArray("music_dna")
                for (i in 0 until musicArray.length()) {
                    val item = musicArray.getJSONObject(i)
                    val name = item.getString("name")
                    com.uriel.logpose.thamis.learning.LearningEngine.registerMusicTrack(name, name)
                }
            }
            com.uriel.logpose.core.compat.core.LogPoseLogger.i("KnowledgeBase: Semilla Staff hidratada exitosamente desde assets.")
        } catch (e: Exception) {
            com.uriel.logpose.core.compat.core.LogPoseLogger.w("KnowledgeBase: Error hidratando staff_seed.json: ${e.message}")
        }
    }
}
