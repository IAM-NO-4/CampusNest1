package com.campusnest1.groupq.ui

import com.campusnest1.groupq.model.Hostel
import com.campusnest1.groupq.model.Room

object MockData{
    val mockHostels = listOf(
        Hostel(
            hostelId = "1",
            name = "Sunrise Student Nest",
            location = "North Campus Avenue, Block B",
            lowestPrice = "350",
            highestPrice = "500",
            imageUrl = "https://example.com/image1.jpg", // Coil will use this later
            rating = 4.8,
            distance = "0.5 km from Main Gate",
        ),
        Hostel(
            hostelId = "2",
            name = "New Nana Hostel",
            location = "LDC Road, Makerere-Kagugube",
            lowestPrice = "280",
            highestPrice = "400",
            imageUrl = "https://example.com/image2.jpg",
            rating = 4.5,
            distance = "1.2 km from Main Gate"
        ),
        Hostel(
            hostelId = "3",
            name = "Kagugube Road, Kampala",
            location = "Main St. 45",
            lowestPrice = "150",
            highestPrice = "250",
            imageUrl = "https://example.com/image3.jpg",
            rating = 4.1,
            distance = "2.5 km from Main Gate"
        )
    )

    val mockRooms = listOf(
        Room(type = "Premium Single", price = 450.0, isAvailable = false),
        Room(type = "Shared Double", price = 300.0, isAvailable = true)
    )


}