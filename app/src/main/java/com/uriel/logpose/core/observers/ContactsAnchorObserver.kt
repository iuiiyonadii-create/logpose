package com.uriel.logpose.core.observers

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.engine.AnchorRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ContactsAnchorObserver v4.5: Hidratador de Agenda.
 * Mantiene el AnchorRepository sincronizado con los contactos del sistema.
 */
class ContactsAnchorObserver(
    private val context: Context,
    private val anchorRepository: AnchorRepository,
    private val scope: CoroutineScope
) : ContentObserver(Handler(Looper.getMainLooper())) {

    fun register() {
        if (context.checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            return
        }
        try {
            context.contentResolver.registerContentObserver(
                ContactsContract.Contacts.CONTENT_URI,
                true,
                this
            )
            syncContacts()
        } catch (e: Exception) { LogPoseLogger.w("Suppressed: ${e.message}") }
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        syncContacts()
    }

    private fun syncContacts() {
        if (context.checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            return
        }
        scope.launch(Dispatchers.IO) {
            try {
                val contacts = mutableListOf<String>()
                val cursor = context.contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY),
                    "${ContactsContract.Contacts.HAS_PHONE_NUMBER} = 1",
                    null,
                    null
                )
                cursor?.use {
                    val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
                    while (it.moveToNext()) {
                        if (nameIndex >= 0) {
                            it.getString(nameIndex)?.let { name -> 
                                if (name.length > 2) contacts.add(name)
                            }
                        }
                    }
                }
                anchorRepository.updateContacts(contacts)
            } catch (e: Exception) { LogPoseLogger.w("Suppressed: ${e.message}") }
        }
    }

    fun unregister() {
        context.contentResolver.unregisterContentObserver(this)
    }
}
