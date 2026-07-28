package com.uriel.logpose.features.notifications

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.uriel.logpose.core.thamis.Event
import com.uriel.logpose.core.thamis.EventBus
import com.uriel.logpose.core.thamis.EventType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Android service that intercepts incoming notifications.
 */
@AndroidEntryPoint
class NotificationListener : NotificationListenerService() {

    @Inject
    lateinit var eventBus: EventBus

    private val parser = NotificationParser()
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val event = parser.parse(sbn)
        Log.d("NotificationListener", "New notification from: ${event.application}")
        
        scope.launch {
            eventBus.emit(
                Event(
                    type = EventType.MESSAGE_RECEIVED,
                    source = "NotificationListener",
                    data = event
                )
            )
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Handle removal if necessary
    }
}
