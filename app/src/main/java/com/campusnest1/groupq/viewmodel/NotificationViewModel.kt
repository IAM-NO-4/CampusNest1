package com.campusnest1.groupq.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.campusnest1.groupq.ui.MockData

class NotificationViewModel : ViewModel() {
    var notifications by mutableStateOf(MockData.mockNotification)
        private set

    fun markAsRead(notificationId: String) {
        notifications = notifications.map {
            if (it.notificationId == notificationId) it.copy(isRead = true) else it
        }
    }

    fun deleteNotification(notificationId: String) {
        notifications = notifications.filter { it.notificationId != notificationId }
    }
}
