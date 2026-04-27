package com.campusnest1.groupq.data

import com.campusnest1.groupq.model.Booking
import com.campusnest1.groupq.model.Hostel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class HostelImplementationRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : HostelRepository {

    // Fetches all available hostels for the main list
    override suspend fun getHostels(): List<Hostel> {
        return try {
            val snapshot = db.collection("Hostels").get().await()
            snapshot.documents.mapNotNull { it.toObject(Hostel::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Fetches only the hostels this specific student saved
    override suspend fun getSavedHostels(userId: String): List<Hostel> {
        return try {
            val snapshot = db.collection("users").document(userId)
                .collection("savedHostels").get().await()
            snapshot.documents.mapNotNull { it.toObject(Hostel::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Fetches the student's booking records
    override suspend fun getBookingHistory(userId: String): List<Booking> {
        return try {
            val snapshot = db.collection("bookings")
                .whereEqualTo("studentId", userId).get().await()
            snapshot.documents.mapNotNull { it.toObject(Booking::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun toggleSavedHostel(userId: String, hostelId: String): Boolean {
        return try {
            val docRef = db.collection("users").document(userId)
                .collection("savedHostels").document(hostelId)
            
            val doc = docRef.get().await()
            if (doc.exists()) {
                docRef.delete().await()
                false // Removed from favorites
            } else {
                // Fetch hostel data to save it (or just save the ID if preferred, 
                // but the current getSavedHostels expects full objects)
                val hostelDoc = db.collection("Hostels").document(hostelId).get().await()
                if (hostelDoc.exists()) {
                    docRef.set(hostelDoc.data!!).await()
                }
                true // Added to favorites
            }
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun isHostelSaved(userId: String, hostelId: String): Boolean {
        return try {
            val doc = db.collection("users").document(userId)
                .collection("savedHostels").document(hostelId).get().await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }
}
