package com.uriel.logpose.core.services

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.communication.model.ContactCandidate
import com.uriel.logpose.thamis.communication.resolver.ContactResolver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Gestiona el acceso a la agenda para alimentar el motor de contactos y la gramática.
 * Mejorado (Misión #013): Monitoreo de cambios en tiempo real.
 */
object ContactManager {

    private var observer: ContentObserver? = null

    /**
     * Sincroniza los contactos del sistema con el motor de inteligencia de THAMIS.
     */
    suspend fun syncContacts(context: Context, limit: Int = 100) = withContext(Dispatchers.IO) {
        if (context.checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            LogPoseLogger.w("ContactManager: Sin permiso de contactos.")
            return@withContext
        }

        val candidates = mutableListOf<ContactCandidate>()
        val contentResolver = context.contentResolver
        
        try {
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.STARRED,
                    ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED
                ),
                null,
                null,
                "${ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED} DESC LIMIT $limit"
            )

            cursor?.use {
                val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val starredIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.STARRED)
                val timesIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED)

                while (it.moveToNext()) {
                    val id = it.getString(idIndex) ?: ""
                    val name = it.getString(nameIndex) ?: continue
                    val number = it.getString(numberIndex) ?: ""
                    val isFavorite = it.getInt(starredIndex) > 0
                    val frequency = it.getInt(timesIndex)

                    candidates.add(ContactCandidate(
                        id = id,
                        name = name,
                        phoneNumber = number,
                        isFavorite = isFavorite,
                        callFrequency = frequency
                    ))
                }
            }
            
            ContactResolver.populate(candidates)
            LogPoseLogger.i("ContactManager: Sincronizados ${candidates.size} contactos con THAMIS.")
            
            // Registramos observador si no existe
            if (observer == null) {
                registerObserver(context)
            }
        } catch (e: Exception) {
            LogPoseLogger.e("ContactManager: Error al sincronizar contactos: ${e.message}")
        }
    }

    private fun registerObserver(context: Context) {
        observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                LogPoseLogger.i("ContactManager: Cambio detectado en la agenda. Re-sincronizando...")
                // Lanzamos sincronización asíncrona
                kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                    syncContacts(context)
                    // Actualizamos gramática de Vosk
                    LogPoseApplication.entryPoint.voskVoiceEngine().updateGrammar()
                }
            }
        }
        context.contentResolver.registerContentObserver(
            ContactsContract.Contacts.CONTENT_URI,
            true,
            observer!!
        )
    }

    @Deprecated("Usar syncContacts para el motor inteligente")
    fun getTopContactNames(context: Context, limit: Int = 100): List<String> {
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
                val cleanName = fullName.split(" ")
                    .firstOrNull()
                    ?.lowercase()
                    ?.replace(Regex("[^a-zñáéíóú]"), "") ?: ""
                
                if (cleanName.length > 2) {
                    names.add(cleanName)
                }
            }
        }
        return names.distinct()
    }
}
