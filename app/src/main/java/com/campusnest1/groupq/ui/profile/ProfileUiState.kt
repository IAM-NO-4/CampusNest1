package com.campusnest1.groupq.ui.profile

data class ProfileUiState(
    val profileId: String = "",
    val userId: String = "",
    val fname: String = "",
    val lname: String = "",
    val email: String = "",
    val phone: String = "",
    val profileImageUrl: String? = null,
    val course: String? = "",
    val yearOfStudy: String? = "",
    val currentHostel: String = "",
    val currentRoomNo: String = "",
    val favHostels: String? = "",
    val priceChangeNotify: Boolean = true,
    val newEventNotify: Boolean = true,
    val roomAvailabilityNotify: Boolean = true,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
