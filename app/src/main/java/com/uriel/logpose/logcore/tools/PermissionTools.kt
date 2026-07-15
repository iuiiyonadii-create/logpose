package com.uriel.logpose.logcore.tools

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Utilidades para consultar permisos en tiempo de ejecución de Android.
 * No solicita permisos (eso requiere una Activity/Fragment); solo consulta su estado.
 * Requiere el SDK de Android y `androidx.core:core-ktx` para compilar.
 */
object PermissionTools {

    /** Indica si [permission] está concedido para [context]. */
    fun isGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    /** Indica si todos los [permissions] están concedidos para [context]. */
    fun areAllGranted(context: Context, permissions: List<String>): Boolean {
        return permissions.all { isGranted(context, it) }
    }

    /** Devuelve la lista de [permissions] que aún no están concedidos para [context]. */
    fun missingPermissions(context: Context, permissions: List<String>): List<String> {
        return permissions.filterNot { isGranted(context, it) }
    }
}
