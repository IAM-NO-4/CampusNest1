package com.campusnest1.groupq.model

data class Profile(
    val fname: String = "",
    val lname: String = "",
    val email: String = "",
    val phone: String = "",
    val profileImageUrl: String = "",
    val userId: String = "",
    val course: String? = "",
    val yearOfStudy: String? = "",
    val currentHostel: String = "",
    val currentRoomNo: String = "",
    val favHostels: String? = "",
    val priceChangeNotify: Boolean = true,
    val newEventNotify: Boolean = true,
    val roomAvailabilityNotify: Boolean = true
)
