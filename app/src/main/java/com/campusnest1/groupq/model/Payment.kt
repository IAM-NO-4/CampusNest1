package com.campusnest1.groupq.model

data class payment(
    val bookingId: String = "",
    val paymentId: String = "",
    val userId: String = "",
    val hostelId: String = "",
    val amount: Double = 0.0,
    val paymentDate: Long = System.currentTimeMillis(),
    val paymentMethod: String = "",
    val status: String = ""



)