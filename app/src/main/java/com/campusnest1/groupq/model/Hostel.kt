package com.campusnest1.groupq.model

import com.campusnest1.groupq.model.Room

data class Hostel(
    //Added availableRooms, distance, amenities and image url
    val hostelId: String = "",
    val name: String = "",
    val location: Any = "",
    val lowestPrice: String = "",
    val highestPrice: String = "",
    val ownerId: String = "",
    val distance: String = "",
    val imageUrl: String = "",
    val rating: Double = 0.0, // Duplicate entry in review entity
    val description: String = "",
    val amenities: List<String> = emptyList(),
    val availableRooms: Int = 0,
    val rooms: List<Room> = emptyList()
)