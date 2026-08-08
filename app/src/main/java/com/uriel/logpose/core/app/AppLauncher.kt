package com.uriel.logpose.core.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.services.AlertManager
import com.uriel.logpose.features.voice.MusicVocabulary
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface AppLauncher {
    fun openApp(appName: String)
    fun closeApp(appName: String)
}

@Singleton
class AppLauncherImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AppLauncher {

    private val APP_REGISTRY = listOf(
        AppEntry("com.whatsapp", listOf("whatsapp", "wasap", "guasap", "wpp", "sapo", "wasapp")),
        AppEntry("com.google.android.apps.maps", listOf("google maps", "maps", "mapas", "waze", "gps", "navegador", "camino")),
        AppEntry("com.google.android.youtube", listOf("youtube", "yt", "iutub", "yutu")),
        AppEntry("com.google.android.apps.youtube.music", listOf("youtube music", "yt music", "iutub music", "yutu music", "musica")),
        AppEntry("com.spotify.music", listOf("spotify", "spoty", "música", "musica", "espotifai")),
        AppEntry("com.google.android.gm", listOf("gmail", "correo", "mail")),
        AppEntry("com.instagram.android", listOf("instagram", "insta", "ig", "fotos", "instagran")),
        AppEntry("com.android.chrome", listOf("chrome", "google", "navegador", "internet")),
        AppEntry("com.waze", listOf("waze", "guaze", "avisos")),
        AppEntry("com.zhiliaoapp.musically", listOf("tiktok", "tictoc", "tito", "videos")),
        AppEntry("com.discord", listOf("discord", "discor", "diskort", "comunidad")),
        AppEntry("com.twitter.android", listOf("twitter", "tuiter", "equis", "x")),
        AppEntry("com.facebook.katana", listOf("facebook", "feibu", "caralibro"))
    )

    private data class AppEntry(val packageId: String, val aliases: List<String>)

    override fun openApp(appName: String) {
        if (appName.contains("://")) {
            launchUri(appName)
            return
        }

        // v4.6.3: Filtro de Objetivo Inválido (Guardrail contra ruido/verbos sueltos)
        val cleanAppTarget = appName
            .replace(Regex("(?i)^(abri|abrir|abrí|entra|entrar|abre|lanzar|arranca|poner|pone|poné)\\s+"), "")
            .trim()

        if (cleanAppTarget.isBlank() || cleanAppTarget.length < 2) {
            LogPoseLogger.w("AppLauncher: Intento de apertura con objetivo inválido: '$appName'")
            return 
        }

        val resolvedPackage = resolveAppName(cleanAppTarget)
        
        if (resolvedPackage != null) {
            launchViaTrampoline(resolvedPackage)
        } else {
            LogPoseLogger.w("AppLauncher: No exact match for '$cleanAppTarget', trying fuzzy/partial...")
            tryLaunchByPartialName(cleanAppTarget)
        }
    }

    private fun launchUri(uriString: String) {
        try {
            LogPoseLogger.i("AppLauncher: Lanzando URI: $uriString")
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            LogPoseLogger.e("AppLauncher: Error al lanzar URI: ${e.message}")
            AlertManager.enqueue("No pude procesar la acción de la aplicación.")
        }
    }

    private fun resolveAppName(input: String): String? {
        val normalized = MusicVocabulary.normalize(input)
        
        // 1. Exact match in aliases
        APP_REGISTRY.forEach { entry ->
            if (entry.aliases.contains(normalized)) return entry.packageId
        }

        // 2. Fuzzy match
        var bestMatch: String? = null
        var bestScore = 0.75
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

    private fun launchViaTrampoline(packageName: String) {
        val targetIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (targetIntent != null) {
            targetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            
            try {
                LogPoseLogger.i("AppLauncher: Lanzando trampolín para $packageName")
                val trampolineIntent = Intent(context, TrampolineActivity::class.java).apply {
                    putExtra("TARGET_INTENT", targetIntent)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
                }
                context.startActivity(trampolineIntent)
            } catch (e: Exception) {
                LogPoseLogger.e("AppLauncher: Fallo en trampolín: ${e.message}")
                context.startActivity(targetIntent)
            }
        } else {
            AlertManager.enqueue("No encontré la aplicación con paquete $packageName.")
        }
    }

    private fun tryLaunchByPartialName(name: String) {
        val pm = context.packageManager
        val apps = pm.queryIntentActivities(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0)
        val cleanName = name.lowercase().trim()
        
        for (app in apps) {
            if (app.loadLabel(pm).toString().lowercase().contains(cleanName)) {
                LogPoseLogger.i("AppLauncher: Coincidencia encontrada: ${app.activityInfo.packageName}")
                launchViaTrampoline(app.activityInfo.packageName)
                return
            }
        }
        AlertManager.enqueue("No encontré ninguna aplicación llamada $name.")
    }

    override fun closeApp(appName: String) {
        LogPoseLogger.d("AppLauncher: Cerrar app $appName (No implementado - requiere root/accessibility)")
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
}
