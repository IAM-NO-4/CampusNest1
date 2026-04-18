package com.campusnest1.groupq.entities

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
//    //This is code for the favourites function. Will be needed in week 3.
//    val favouriteHostels: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)