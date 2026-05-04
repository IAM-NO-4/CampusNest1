package com.campusnest1.groupq.model

data class User(
    val userId: String = "",
    val fname: String = "",
    val lname: String = "",
    val email: String = "",
    val phone: String = "",
    var profileImageUrl: String? = null
)