package com.uriel.logpose.logcore.tools

import android.content.Context
import android.net.Uri
import java.io.File

/**
 * Utilidades para trabajar con [Uri], incluyendo esquemas `content://` de Android.
 * Requiere el SDK de Android para compilar.
 */
object UriTools {

    /** Crea un [Uri] a partir de una ruta de archivo local. */
    fun fromFile(file: File): Uri = Uri.fromFile(file)

    /** Indica si [uri] usa el esquema `content://`. */
    fun isContentUri(uri: Uri): Boolean = uri.scheme == "content"

    /** Indica si [uri] usa el esquema `file://`. */
    fun isFileUri(uri: Uri): Boolean = uri.scheme == "file"

    /** Devuelve el nombre de archivo mostrado por [uri] usando el `ContentResolver` de [context]. */
    fun displayName(context: Context, uri: Uri): String? {
        if (!isContentUri(uri)) return uri.lastPathSegment
        val cursor = context.contentResolver.query(uri, null, null, null, null) ?: return uri.lastPathSegment
        return cursor.use {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && it.moveToFirst()) it.getString(nameIndex) else uri.lastPathSegment
        }
    }

    /** Lee todo el contenido apuntado por [uri] como bytes, usando el `ContentResolver` de [context]. */
    fun readBytes(context: Context, uri: Uri): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { StreamTools.readAllBytes(it) }
        } catch (e: Exception) {
            null
        }
    }

    /** Devuelve el valor de un parámetro de query de [uri], o `null` si no existe. */
    fun queryParam(uri: Uri, name: String): String? = uri.getQueryParameter(name)
}
