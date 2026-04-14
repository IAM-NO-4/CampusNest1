package com.campusnest1.groupq.model

data class Hostel(
    val hostelId: String = "",
    val name: String = "",
    val location: Any = "",
    val lowestPrice: String = "",
    val highestPrice: String = "",
    val ownerId: String = "",
    val rating: Double = 0.0,
    val description: String = ""
)