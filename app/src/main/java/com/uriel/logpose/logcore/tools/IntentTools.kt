package com.uriel.logpose.logcore.tools

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Utilidades para construir e inspeccionar [Intent], sin lanzar Activities directamente
 * (esa responsabilidad queda del lado de la capa de UI de cada módulo).
 * Requiere el SDK de Android para compilar.
 */
object IntentTools {

    /** Crea un intent para compartir texto plano. */
    fun shareText(text: String, subject: String? = null): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            subject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
        }
    }

    /** Crea un intent para abrir una URL en el navegador. */
    fun openUrl(url: String): Intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

    /** Crea un intent para abrir la pantalla de ajustes de la app. */
    fun openAppSettings(context: Context): Intent {
        return Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
    }

    /** Indica si existe al menos una app capaz de resolver [intent]. */
    fun canResolve(context: Context, intent: Intent): Boolean {
        return intent.resolveActivity(context.packageManager) != null
    }

    /** Devuelve una copia de [intent] envuelta en un selector ("Abrir con..."), usando [title]. */
    fun withChooser(intent: Intent, title: String? = null): Intent {
        return Intent.createChooser(intent, title)
    }
}
