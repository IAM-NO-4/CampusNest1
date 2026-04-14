package com.campusnest1.groupq.model

data class Manager(
    val managerId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

