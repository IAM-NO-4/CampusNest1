package com.campusnest1.groupq.model

data class Event(
    val eventId: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val location: String = "",
    val category: String = "",
    val imageUrl: String = "",
    val registrationUrl: String = "",
)
