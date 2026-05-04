package com.campusnest1.groupq.data

import com.campusnest1.groupq.model.Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EventImplementationRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : EventRepository {

    override suspend fun getEvents(): List<Event> {
        return try {
            val snapshot = db.collection("Events").get().await()
            snapshot.documents.mapNotNull {
                it.toObject(Event::class.java)?.copy(eventId = it.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun toggleSavedEvent(
        userId: String,
        eventId: String
    ): Boolean {
        return try {
            val docRef = db.collection("users").document(userId)
                .collection("/User/${userId}/savedEvents").document(eventId)

            val doc = docRef.get().await()
            if (doc.exists()) {
                docRef.delete().await()
                false
            } else {
                val eventDoc = db.collection("Events").document(eventId).get().await()
                if (eventDoc.exists()) {
                    docRef.set(eventDoc.data!!).await()
                }
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun isEventSaved(
        userId: String,
        eventId: String
    ): Boolean {
        return try {
            val doc = db.collection("users").document(userId)
                .collection("/User/${userId}/savedEvents").document(eventId).get().await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }
}
