package com.campusnest1.groupq.model

data class Notification(
    val notificationId: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val category: String = "",
    val isRead: Boolean = false,
    val targetId: String? = null
)
