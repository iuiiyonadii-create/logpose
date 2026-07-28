package com.uriel.logpose.data.notifications

import com.uriel.logpose.domain.repositories.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor() : NotificationRepository {
    private var isEnabled = true

    override fun setNotificationReadingEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    override fun isReadingEnabled(): Boolean = isEnabled
}
