package com.campusnest1.groupq.data

import com.campusnest1.groupq.model.Booking
import com.campusnest1.groupq.model.Hostel
import com.campusnest1.groupq.model.Room
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class HostelImplementationRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : HostelRepository {

    override suspend fun getHostels(): List<Hostel> {
        return try {
            val snapshot = db.collection("hostels").get().await()
            snapshot.documents.mapNotNull { doc ->
                mapDocumentToHostel(doc)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getHostelById(hostelId: String): Hostel? {
        if (hostelId.isEmpty()) return null
        return try {
            val doc = db.collection("hostels").document(hostelId).get().await()
            if (!doc.exists()) return null
            mapDocumentToHostel(doc)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getRoomsForHostel(hostelId: String): List<Room> {
        if (hostelId.isEmpty()) return emptyList()
        return try {
            // Based on screenshot: rooms is a ROOT collection, not a subcollection.
            // We fetch documents where the 'hostelId' field matches the current hostel.
            val snapshot = db.collection("rooms")
                .whereEqualTo("hostelId", hostelId)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                Room(
                    roomId = doc.id,
                    hostelId = data["hostelId"] as? String ?: hostelId,
                    hostelName = data["hostelName"] as? String ?: "",
                    // Safely handles both String and Number types from Firestore
                    price = data["price"]?.toString() ?: "",
                    type = data["type"] as? String ?: "",
                    isAvailable = data["isAvailable"] as? Boolean ?: true,
                    beds = (data["beds"] as? Number)?.toInt() ?: 1,
                    capacity = (data["capacity"] as? Number)?.toInt() ?: 1,
                    status = data["status"] as? String ?: ""
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getSavedHostels(userId: String): List<Hostel> {
        if (userId.isEmpty()) return emptyList()
        return try {
            val snapshot = db.collection("users").document(userId)
                .collection("savedHostels").get().await()
            snapshot.documents.mapNotNull { doc ->
                mapDocumentToHostel(doc)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getBookingHistory(userId: String): List<Booking> {
        if (userId.isEmpty()) return emptyList()
        return try {
            val snapshot = db.collection("bookings")
                .whereEqualTo("studentId", userId).get().await()
            snapshot.documents.mapNotNull { it.toObject(Booking::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun toggleSavedHostel(userId: String, hostelId: String): Boolean {
        if (userId.isEmpty() || hostelId.isEmpty()) return false
        return try {
            val docRef = db.collection("users").document(userId)
                .collection("savedHostels").document(hostelId)
            
            val doc = docRef.get().await()
            if (doc.exists()) {
                docRef.delete().await()
                false 
            } else {
                val hostelDoc = db.collection("hostels").document(hostelId).get().await()
                if (hostelDoc.exists()) {
                    val data = hostelDoc.data?.toMutableMap() ?: mutableMapOf()
                    data["hostelId"] = hostelId
                    docRef.set(data).await()
                    true 
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun isHostelSaved(userId: String, hostelId: String): Boolean {
        if (userId.isEmpty() || hostelId.isEmpty()) return false
        return try {
            val doc = db.collection("users").document(userId)
                .collection("savedHostels").document(hostelId).get().await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }

    private fun mapDocumentToHostel(doc: com.google.firebase.firestore.DocumentSnapshot): Hostel? {
        val data = doc.data ?: return null
        return Hostel(
            hostelId = doc.id,
            name = data["name"] as? String ?: "",
            location = data["location"] ?: "",
            lowestPrice = data["lowestPrice"]?.toString() ?: "",
            highestPrice = data["highestPrice"]?.toString() ?: "",
            managerId = data["managerId"] as? String ?: "",
            distance = data["distance"] as? String ?: "",
            imageUrl = data["imageUrl"] as? String ?: "",
            avgRating = (data["avgRating"] as? Number)?.toDouble() ?: 0.0,
            description = data["description"] as? String ?: "",
            amenities = (data["amenities"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            availableRooms = (data["availableRooms"] as? Number)?.toInt() ?: 0,
            roomTypes = (data["roomTypes"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
        )
    }
}
