package com.campusnest1.groupq.model

data class Room(
    val roomId: String = "",
    val hostelId: String = "",
    val hostelName: String = "",
    val price: Double = 0.0,
    val type: String = "",
    val isAvailable: Boolean = true
)