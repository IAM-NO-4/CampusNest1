package com.campusnest1.groupq.model

import android.text.Highlights
import java.net.URL

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
    val highlights: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val fee: String = "0",
    val attendees: String = "Open to the general public",
    val eventOrganizer: String = "",
    val eventOrganizerImageURL: String = ""
)
