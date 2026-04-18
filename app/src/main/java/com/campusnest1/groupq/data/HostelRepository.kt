package com.campusnest1.groupq.data

import com.campusnest1.groupq.entities.Hostel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class HostelRepository(
    private val db: FirebaseFirestore
) {
    suspend fun getHostels(): List<Hostel> {
        return try {
            val snapshot = db.collection("Hostels").get().await()
            snapshot.documents.mapNotNull {
                it.toObject(Hostel::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}