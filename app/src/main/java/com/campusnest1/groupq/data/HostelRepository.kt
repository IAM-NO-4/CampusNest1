package com.campusnest1.groupq.data

import com.campusnest1.groupq.model.Booking
import com.campusnest1.groupq.model.Hostel
import com.campusnest1.groupq.model.Room

interface HostelRepository {
    suspend fun getHostels(): List<Hostel>
    suspend fun getHostelById(hostelId: String): Hostel?
    suspend fun getRoomsForHostel(hostelId: String): List<Room>
    suspend fun getSavedHostels(userId: String): List<Hostel>
    suspend fun getBookingHistory(userId: String): List<Booking>
    suspend fun toggleSavedHostel(userId: String, hostelId: String): Boolean
    suspend fun isHostelSaved(userId: String, hostelId: String): Boolean
    suspend fun getManagerContact(managerId: String): String?
}