package com.campusnest1.groupq.data

import com.campusnest1.groupq.model.Notification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class NotificationRepositoryImpl(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : NotificationRepository {

    private val notificationRef = db.collection("notifications")

    override suspend fun getNotifications(userId: String): List<Notification> {
        return try {
            notificationRef
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(Notification::class.java) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun markAsRead(notificationId: String) {
        try {
            notificationRef.document(notificationId).update("isRead", true).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteNotification(notificationId: String) {
        try {
            notificationRef.document(notificationId).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun addNotification(notification: Notification) {
        try {
            notificationRef.document(notification.notificationId).set(notification).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
