package com.campusnest1.groupq.model

data class Room(
    //Added Beds, Status and Capacity
    val roomId: String = "",
    val hostelId: String = "",
    val hostelName: String = "",
    val price: Double = 0.0,
    val type: String = "",
    val isAvailable: Boolean = true,
    val beds: Int = 1,
    val capacity: Int = 1,
    val status: String = "" // e.g., "Almost Full", "2 Left"
)
