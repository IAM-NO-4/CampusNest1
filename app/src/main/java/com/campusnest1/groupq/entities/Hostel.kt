package com.campusnest1.groupq.entities

data class Hostel(
    //Added distance, amenities and image url
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
)