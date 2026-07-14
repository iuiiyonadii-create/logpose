package com.uriel.logpose.thamis.knowledge

import com.uriel.logpose.thamis.knowledge.calls.CallKnowledge
import com.uriel.logpose.thamis.knowledge.emergency.EmergencyKnowledge
import com.uriel.logpose.thamis.knowledge.maps.NavigationKnowledge
import com.uriel.logpose.thamis.knowledge.music.MusicKnowledge
import com.uriel.logpose.thamis.knowledge.weather.WeatherKnowledge

/**
 * Reúne todo el conocimiento disponible de THAMIS.
 */
object KnowledgeBase {

    val entries: List<KnowledgeRule> by lazy {

        buildList {

            addAll(MusicKnowledge.rules)

            addAll(CallKnowledge.rules)

            addAll(NavigationKnowledge.rules)

            addAll(WeatherKnowledge.rules)

            addAll(EmergencyKnowledge.rules)

        }

    }

}