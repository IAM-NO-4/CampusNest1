package com.campusnest1.groupq.model

data class Profile(
    val userId: String = "",
    val course: String = "",
    val yearOfStudy: String = "",
    val currentHostel: String = "",
    val currentRoomNo: String ="",
    val favHostels: String = "" //<- I SUGGEST THIS BE REMOVED, IT CAN BE DERIVED FROM THE FAVORITE MODEL

)