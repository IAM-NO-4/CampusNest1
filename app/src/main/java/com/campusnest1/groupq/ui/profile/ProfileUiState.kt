package com.campusnest1.groupq.ui.profile

data class ProfileUiState(
    val profileId: String = "",
    val userId: String = "",
    val course: String? = "",
    val yearOfStudy: String? = "",
    val currentHostel: String? = "",
    val currentRoomNo: String = "",
    val favHostels: String? = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
