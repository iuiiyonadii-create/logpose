package com.uriel.logpose.logprobe.media

import android.content.ComponentName
import android.content.Context
import android.media.session.MediaSessionManager
import com.uriel.logpose.logprobe.common.ProbeGuard
import com.uriel.logpose.logprobe.models.MediaSnapshot

/**
 * Lee sesiones de Media activas usando MediaSessionManager, API publica.
 * Requiere que el usuario haya otorgado "Notification Listener access"
 * (el mismo permiso que ya usa NotificationProbeService); no se agrega
 * ningun permiso extra por esto.
 *
 * Solo devuelve snapshots de paquetes permitidos por ProbeGuard: las
 * sesiones de mensajeria (si alguna expusiera MediaSession, caso raro
 * pero posible) tambien quedan filtradas aca.
 */
class MediaProbe(
    private val context: Context,
    private val notificationListenerComponent: ComponentName
) {
    private val manager =
        context.applicationContext.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager

    fun currentSnapshots(): List<MediaSnapshot> {
        val controllers = try {
            manager?.getActiveSessions(notificationListenerComponent).orEmpty()
        } catch (_: SecurityException) {
            emptyList()
        }

        return controllers
            .filter { ProbeGuard.isPackageAllowed(it.packageName) }
            .map { controller -> MediaParser.parse(controller.packageName, controller) }
    }
}
