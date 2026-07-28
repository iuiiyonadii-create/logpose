package com.uriel.logpose.thamis.knowledge

import com.uriel.logpose.thamis.intent.Intent
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
import com.uriel.logpose.thamis.knowledge.weather.WeatherKnowledge

/**
 * Reúne todo el conocimiento disponible de THAMIS.
 */
object KnowledgeBase {

    val entries: List<KnowledgeRule> by lazy {
        buildList {
            addAll(NavigationKnowledge.rules) // Prioridad absoluta a navegación
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
            
            // Comandos de sistema integrados
            add(KnowledgeRule(Intent.OPEN_APP, setOf("logpose privacidad", "logpose volver", "logpose estado", "logpose apagar", "logpose desactivar")))
        }
    }
}
