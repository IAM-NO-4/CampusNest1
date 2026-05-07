package com.campusnest1.groupq.data

import com.campusnest1.groupq.model.Notification

interface NotificationRepository {

    suspend fun getNotifications(userId: String): List<Notification>

    suspend fun markAsRead(notificationId: String)

    suspend fun deleteNotification(notificationId: String)

    suspend fun addNotification(notification: Notification)
}