package com.uriel.logpose.logprobe.notification

import android.service.notification.StatusBarNotification
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Punto de distribucion (fan-out) de los callbacks que Android entrega a
 * NotificationProbeService. Introducido por el Sprint Notification Core
 * (Decision 2): NotificationProbeService deja de tener un unico consumidor
 * (LogProbe) y pasa a reenviar cada evento crudo a todos los consumidores
 * internos registrados.
 *
 * Esta clase NO contiene logica de negocio ni de admision: solo reenvia el
 * StatusBarNotification tal cual a cada [NotificationEventListener]. Cada
 * consumidor decide por su cuenta si procesa el evento y con que reglas
 * (LogProbe sigue usando ProbeGuard exclusivamente para si mismo; Notification
 * Core usa su propia NotificationSession — ver Decision 3 / Decision 8).
 */
fun interface NotificationEventListener {

    /**
     * Se llama para cada notificacion publicada o removida. [removed] indica
     * cual de los dos ocurrio, ya que ambos casos entregan el mismo
     * StatusBarNotification crudo y cada listener suele reutilizar la misma
     * logica de parseo para ambos.
     */
    fun onNotificationEvent(sbn: StatusBarNotification, removed: Boolean)
}

object NotificationEventBus {

    private val listeners = CopyOnWriteArrayList<NotificationEventListener>()

    /** Idempotente: registrar el mismo listener dos veces no lo duplica. */
    fun register(listener: NotificationEventListener) {
        listeners.addIfAbsent(listener)
    }

    fun unregister(listener: NotificationEventListener) {
        listeners.remove(listener)
    }

    fun dispatchPosted(sbn: StatusBarNotification) {
        listeners.forEach { it.onNotificationEvent(sbn, removed = false) }
    }

    fun dispatchRemoved(sbn: StatusBarNotification) {
        listeners.forEach { it.onNotificationEvent(sbn, removed = true) }
    }
}
