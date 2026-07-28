package com.uriel.logpose.core.services

import android.content.Context
import android.provider.ContactsContract
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Gestiona el acceso a la agenda para alimentar la gramática de Vosk.
 */
object ContactManager {

    fun getTopContactNames(context: Context, limit: Int = 100): List<String> {
        if (context.checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            LogPoseLogger.w("ContactManager: Sin permiso de contactos.")
            return emptyList()
        }
        val names = mutableListOf<String>()
        val contentResolver = context.contentResolver
        
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            arrayOf(ContactsContract.Contacts.DISPLAY_NAME),
            null,
            null,
            "${ContactsContract.Contacts.TIMES_CONTACTED} DESC LIMIT $limit"
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            while (it.moveToNext()) {
                val fullName = it.getString(nameIndex) ?: continue
                // Limpiamos el nombre para Vosk (solo la primera palabra o nombres simples)
                val cleanName = fullName.split(" ")
                    .firstOrNull()
                    ?.lowercase()
                    ?.replace(Regex("[^a-zñáéíóú]"), "") ?: ""
                
                if (cleanName.length > 2) {
                    names.add(cleanName)
                }
            }
        }
        
        LogPoseLogger.i("ContactManager: Extraídos ${names.size} nombres para gramática.")
        return names.distinct()
    }
}
