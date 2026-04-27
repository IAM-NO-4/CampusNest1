package com.campusnest1.groupq.model

data class Notification(
    val notificationId: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "",
    val isRead: Boolean = false
) {

}