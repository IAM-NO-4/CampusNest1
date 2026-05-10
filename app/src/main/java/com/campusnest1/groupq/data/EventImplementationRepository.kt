package com.campusnest1.groupq.data

import android.util.Log
import com.campusnest1.groupq.model.Event
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import com.campusnest1.groupq.utils.isEventLive

class EventImplementationRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : EventRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormatDisplay = SimpleDateFormat("h:mm a", Locale.getDefault())

    override suspend fun getEvents(): List<Event> {
        return try {
            Log.d("EventRepo", "Fetching events from collection 'events'...")
            var snapshot = db.collection("events").get().await()
            
            if (snapshot.isEmpty) {
                Log.d("EventRepo", "Collection 'events' is empty. Trying 'Events'...")
                snapshot = db.collection("Events").get().await()
            }

            Log.d("EventRepo", "Found ${snapshot.size()} documents. Processing...")
            val mappedEvents = processDocuments(snapshot.documents)
            Log.d("EventRepo", "Successfully mapped ${mappedEvents.size} events")
            mappedEvents
        } catch (e: Exception) {
            Log.e("EventRepo", "Error fetching events: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
    
    private fun processDocuments(documents: List<com.google.firebase.firestore.DocumentSnapshot>): List<Event> {
        return documents.mapNotNull { doc ->
            try {
                val data = doc.data ?: return@mapNotNull null
                
                // Robust date parsing (Timestamp or String)
                val dateValue = data["date"]
                val formattedDate = when (dateValue) {
                    is Timestamp -> dateFormat.format(dateValue.toDate())
                    is String -> dateValue.trim()
                    else -> ""
                }

                // Robust time parsing (Timestamp or String)
                fun parseTimeField(key: String): String {
                    val value = data[key]
                    return when (value) {
                        is Timestamp -> timeFormatDisplay.format(value.toDate())
                        is String -> value.trim()
                        else -> ""
                    }
                }

                val eventDate = formattedDate.ifEmpty { (data["date"] as? String ?: "") }
                val startTime = parseTimeField("startTime")
                val endTime = parseTimeField("endTime")

                Event(
                    eventId = doc.id,
                    title = data["title"] as? String ?: "Untitled Event",
                    description = data["description"] as? String ?: "",
                    date = eventDate,
                    startTime = startTime,
                    endTime = endTime,
                    location = data["location"] as? String ?: "",
                    category = data["category"] as? String ?: "",
                    imageUrl = data["imageUrl"] as? String ?: "",
                    registrationUrl = data["registrationUrl"] as? String ?: "",
                    highlights = (data["highlights"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                    fee = data["fee"]?.toString() ?: "0",
                    attendees = data["attendees"] as? String ?: "Open to students",
                    eventOrganizer = data["eventOrganizer"] as? String ?: "",
                    eventOrganizerImageURL = data["eventOrganizerImageURL"] as? String ?: "",
                    isLive = isEventLive(eventDate, startTime, endTime)
                ).also {
                    Log.d("EventRepo", "Mapped: ${it.title} | Date: ${it.date} | Time: ${it.startTime}-${it.endTime} | Live: ${it.isLive}")
                }
            } catch (e: Exception) {
                Log.e("EventRepo", "Error mapping document ${doc.id}: ${e.message}")
                null
            }
        }
    }

    override suspend fun toggleSavedEvent(userId: String, eventId: String): Boolean {
        if (userId.isEmpty() || eventId.isEmpty()) return false
        return try {
            val savedEventsRef = db.collection("savedEvents")
            val query = savedEventsRef
                .whereEqualTo("userId", userId)
                .whereEqualTo("eventId", eventId)
                .get()
                .await()

            if (!query.isEmpty) {
                query.documents.forEach { savedEventsRef.document(it.id).delete().await() }
                false
            } else {
                val eventDoc = db.collection("events").document(eventId).get().await()
                val finalEventDoc = if (!eventDoc.exists()) db.collection("Events").document(eventId).get().await() else eventDoc
                
                if (finalEventDoc.exists()) {
                    val data = finalEventDoc.data?.toMutableMap() ?: mutableMapOf()
                    data["userId"] = userId
                    data["eventId"] = eventId
                    savedEventsRef.add(data).await()
                    true
                } else false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun isEventSaved(userId: String, eventId: String): Boolean {
        if (userId.isEmpty() || eventId.isEmpty()) return false
        return try {
            val query = db.collection("savedEvents")
                .whereEqualTo("userId", userId)
                .whereEqualTo("eventId", eventId)
                .limit(1).get().await()
            !query.isEmpty
        } catch (e: Exception) { false }
    }

    override suspend fun deleteEvent(eventId: String): Boolean {
        if (eventId.isEmpty()) return false
        return try {
            db.collection("events").document(eventId).delete().await()
            db.collection("Events").document(eventId).delete().await()
            val query = db.collection("savedEvents").whereEqualTo("eventId", eventId).get().await()
            query.documents.forEach { db.collection("savedEvents").document(it.id).delete().await() }
            true
        } catch (e: Exception) { false }
    }
}
