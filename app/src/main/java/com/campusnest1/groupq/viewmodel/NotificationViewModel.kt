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

    var isLoading by mutableStateOf(false)
        private set

    fun fetchNotifications(userId: String) {
        if (userId.isEmpty()) return
        
        viewModelScope.launch {
            isLoading = true
            try {
                notifications = repository.getNotifications(userId)
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle error gracefully, maybe set an error state
            } finally {
                isLoading = false
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                repository.markAsRead(notificationId)
                notifications = notifications.map {
                    if (it.notificationId == notificationId) {
                        it.copy(isRead = true)
                    } else it
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            try {
                repository.deleteNotification(notificationId)
                notifications = notifications.filter {
                    it.notificationId != notificationId
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addNotification(notification: Notification) {
        viewModelScope.launch {
            try {
                repository.addNotification(notification)
                notifications = listOf(notification) + notifications
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
