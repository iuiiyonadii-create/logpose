package com.uriel.logpose.core.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.uriel.logpose.core.notifications.NotificationHelper

class NotificationDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationHelper.cancelSuggestion(context)
    }
}
