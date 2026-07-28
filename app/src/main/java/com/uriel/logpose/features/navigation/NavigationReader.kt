package com.uriel.logpose.features.navigation

import com.uriel.logpose.features.notifications.NotificationItem
import com.uriel.logpose.features.voice.FeedbackManager
import com.uriel.logpose.core.compat.core.LogPoseLogger

object NavigationReader {

    fun read(item: NotificationItem) {
        val rawText = item.content.previewText ?: item.content.title ?: ""
        if (rawText.isEmpty()) return

        val summary = summarize(rawText)
        LogPoseLogger.i("Navegación resumida: $summary")
        
        // Actualizamos el estado global de navegación
        NavigationManager.updateInstruction(summary)
        
        FeedbackManager.speak(summary)
    }

    private fun summarize(text: String): String {
        var clean = text.lowercase()
            .replace("gire a la ", "")
            .replace("doble a la ", "")
            .replace("manténgase a la ", "mantenga ")
            .replace("en avenida ", "por ")
            .replace("por calle ", "por ")

        // Limpieza de distancias repetitivas
        if (clean.contains("metros") || clean.contains("m") || clean.contains("km")) {
             // Dejamos la distancia pero la hacemos sonar natural
             clean = clean.replace("500 metros", "en quinientos metros")
                 .replace("100 metros", "en cien metros")
                 .replace("1.5 km", "en un kilómetro y medio")
        }

        return clean.trim().capitalize()
    }
    
    private fun String.capitalize() = this.replaceFirstChar { it.uppercase() }
}
