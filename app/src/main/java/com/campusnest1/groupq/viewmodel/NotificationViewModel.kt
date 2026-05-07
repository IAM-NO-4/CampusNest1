package com.campusnest1.groupq.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusnest1.groupq.data.NotificationRepository
import com.campusnest1.groupq.model.Notification
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository: NotificationRepository
) : ViewModel() {

    var notifications by mutableStateOf<List<Notification>>(emptyList())
        private set

    fun fetchNotifications(userId: String) {

        viewModelScope.launch {

            notifications =
                repository.getNotifications(userId)
        }
    }

    fun markAsRead(notificationId: String) {

        viewModelScope.launch {

            repository.markAsRead(notificationId)

            notifications = notifications.map {
                if (it.notificationId == notificationId) {
                    it.copy(isRead = true)
                } else it
            }
        }
    }

    fun deleteNotification(notificationId: String) {

        viewModelScope.launch {

            repository.deleteNotification(notificationId)

            notifications =
                notifications.filter {
                    it.notificationId != notificationId
                }
        }
    }

    fun addNotification(notification: Notification) {

        viewModelScope.launch {

            repository.addNotification(notification)

            notifications =
                listOf(notification) + notifications
        }
    }
}