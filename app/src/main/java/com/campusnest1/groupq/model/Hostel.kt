package com.campusnest1.groupq.model

data class Hostel(
    val hostelId: String = "",
    val name: String = "",
    val location: String = "",
    val lowestPrice: String = "",
    val highestPrice: String = "",
    val managerId: String = "",
    val distance: String = "",
    val imageUrl: String = "",
    val avgRating: Double = 0.0,
    val description: String = "",
    val amenities: List<String> = emptyList(),
    val availableRooms: Int = 0,
    val roomTypes: List<String> = emptyList()
)
