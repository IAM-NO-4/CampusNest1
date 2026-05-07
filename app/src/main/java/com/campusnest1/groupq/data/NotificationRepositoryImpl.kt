package com.campusnest1.groupq.data

import com.campusnest1.groupq.model.Notification
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class NotificationRepositoryImpl(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : NotificationRepository {

    private val notificationRef =
        db.collection("notifications")

    override suspend fun getNotifications(
        userId: String
    ): List<Notification> {

        return notificationRef
            .whereEqualTo("userId", userId)
            .orderBy("createdAt")
            .get()
            .await()
            .documents
            .mapNotNull {
                it.toObject(Notification::class.java)
            }
    }

    override suspend fun markAsRead(
        notificationId: String
    ) {

        notificationRef
            .document(notificationId)
            .update("isRead", true)
            .await()
    }

    override suspend fun deleteNotification(
        notificationId: String
    ) {

        notificationRef
            .document(notificationId)
            .delete()
            .await()
    }

    override suspend fun addNotification(
        notification: Notification
    ) {

        notificationRef
            .document(notification.notificationId)
            .set(notification)
            .await()
    }
}