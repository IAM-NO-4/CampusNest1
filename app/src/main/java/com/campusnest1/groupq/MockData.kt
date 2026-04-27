package com.campusnest1.groupq.ui

import com.campusnest1.groupq.model.Hostel
import com.campusnest1.groupq.model.Room

object MockData{
    val mockHostels = listOf(
        Hostel(
            hostelId = "1",
            name = "Sunrise Student Nest",
            location = "North Campus Avenue, Block B",
            lowestPrice = "350000",
            highestPrice = "500000",
            imageUrl = "https://example.com/image1.jpg", // Coil will use this later
            rating = 4.8,
            distance = "0.5 km from Main Gate",
            amenities = listOf("Free Wi-Fi", "Security"),
            availableRooms = 3,
            rooms = listOf(
                Room(hostelId = "1", hostelName = "Sunrise Student Nest", type = "Single", price = 350000.0, isAvailable = true, status = "Available", capacity = 1, beds = 1),
                Room(hostelId = "1", hostelName = "Sunrise Student Nest", type = "Double", price = 500000.0, isAvailable = true, status = "Available", capacity = 2, beds = 2)
            )
        ),
        Hostel(
            hostelId = "2",
            name = "New Nana Hostel",
            location = "LDC Road, Makerere-Kagugube",
            lowestPrice = "280000",
            highestPrice = "400000",
            imageUrl = "https://example.com/image2.jpg",
            rating = 4.5,
            distance = "1.2 km from Main Gate",
            amenities = listOf("Spa", "Security"),
            availableRooms = 10,
            rooms = listOf(
                Room(hostelId = "2", hostelName = "New Nana Hostel", type = "Double", price = 400000.0, isAvailable = true, status = "Available", capacity = 2, beds = 2),
                Room(hostelId = "2", hostelName = "New Nana Hostel", type = "Triple", price = 600000.0, isAvailable = true, status = "Available", capacity = 3, beds = 3)
            )
        ),
        Hostel(
            hostelId = "3",
            name = "Olympia Hostel",
            location = "Main St. 45",
            lowestPrice = "150000",
            highestPrice = "250000",
            imageUrl = "https://example.com/image3.jpg",
            rating = 4.1,
            distance = "2.5 km from Main Gate",
            amenities = listOf("Free Wi-Fi", "Gym", "Security"),
            rooms = listOf(
                Room(hostelId = "3", hostelName = "Olympia Hostel", type = "Premium Single", price = 250000.0, isAvailable = true, status = "Available", capacity = 1, beds = 1)
            )
        )
    )

    val mockRooms = listOf(
        Room(type = "Premium Single", price = 2000000.0, isAvailable = false, status = "Full", capacity = 1, beds = 1),
        Room(type = "Double", price = 850000.0, isAvailable = true, status = "Available", capacity = 2, beds = 2)
    )


}