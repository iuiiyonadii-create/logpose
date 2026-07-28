package com.uriel.logpose.core.app

import android.content.Context
import android.content.Intent
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.services.AlertManager
import com.uriel.logpose.features.voice.MusicVocabulary

/**
 * AppManager: Resolución fuzzy de aplicaciones y lanzamiento vía Trampolín.
 */
object AppManager {

    private lateinit var context: Context

    private val APP_REGISTRY = listOf(
        AppEntry("com.whatsapp", listOf("whatsapp", "wasap", "guasap", "wpp", "sapo")),
        AppEntry("com.google.android.apps.maps", listOf("google maps", "maps", "mapas", "waze", "gps", "navegador")),
        AppEntry("com.google.android.youtube", listOf("youtube", "yt")),
        AppEntry("com.spotify.music", listOf("spotify", "spoty", "música")),
        AppEntry("com.google.android.gm", listOf("gmail", "correo"))
    )

    data class AppEntry(val packageId: String, val aliases: List<String>)

    fun initialize(context: Context) {
        this.context = context.applicationContext
    }

    fun openApp(appName: String) {
        val resolvedPackage = resolveAppName(appName) ?: appName
        LogPoseLogger.i("AppManager: Lanzando '$resolvedPackage' (input: $appName)")
        
        val intent = context.packageManager.getLaunchIntentForPackage(resolvedPackage) ?: 
                    context.packageManager.getLaunchIntentForPackage(packageIdFromAlias(resolvedPackage))

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val trampolineIntent = Intent(context, com.uriel.logpose.core.app.TrampolineActivity::class.java).apply {
                putExtra("TARGET_INTENT", intent)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
            }
            context.startActivity(trampolineIntent)
        } else {
            AlertManager.enqueue("No encontré la aplicación $appName.")
        }
    }

    private fun resolveAppName(input: String): String? {
        val normalized = MusicVocabulary.normalize(input)
        
        // 1. Exacto en alias
        APP_REGISTRY.forEach { entry ->
            if (entry.aliases.contains(normalized)) return entry.packageId
        }

        // 2. Fuzzy match
        var bestMatch: String? = null
        var bestScore = 0.70
        APP_REGISTRY.forEach { entry ->
            entry.aliases.forEach { alias ->
                val score = levenshteinRatio(normalized, MusicVocabulary.normalize(alias))
                if (score > bestScore) {
                    bestScore = score
                    bestMatch = entry.packageId
                }
            }
        }
        return bestMatch
    }

    private fun packageIdFromAlias(alias: String): String {
        return APP_REGISTRY.find { it.aliases.contains(alias) }?.packageId ?: alias
    }

    private fun levenshteinRatio(s1: String, s2: String): Double {
        if (s1 == s2) return 1.0
        val len1 = s1.length; val len2 = s2.length
        if (len1 == 0 || len2 == 0) return 0.0
        val prev = IntArray(len2 + 1)
        val curr = IntArray(len2 + 1)
        for (j in 0..len2) prev[j] = j
        for (i in 1..len1) {
            curr[0] = i
            for (j in 1..len2) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                curr[j] = minOf(prev[j] + 1, curr[j - 1] + 1, prev[j - 1] + cost)
            }
            for (j in 0..len2) prev[j] = curr[j]
        }
        return (maxOf(len1, len2) - prev[len2]).toDouble() / maxOf(len1, len2)
    }

    fun closeApp(appName: String) {
        // Implementación simplificada para el dashboard
        LogPoseLogger.d("AppManager: Cerrar app $appName")
    }
}
