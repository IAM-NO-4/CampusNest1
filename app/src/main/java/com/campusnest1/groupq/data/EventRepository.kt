package com.campusnest1.groupq.data

import com.campusnest1.groupq.model.Event

interface EventRepository {
    suspend fun getEvents(): List<Event>
    suspend fun toggleSavedEvent(userId: String, eventId: String): Boolean
    suspend fun isEventSaved(userId: String, eventId: String): Boolean
}
