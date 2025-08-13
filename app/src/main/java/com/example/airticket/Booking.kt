package com.example.airticket

data class Booking(
    val id: String = "",              // Firestore document ID for deletion
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val age: String = "",
    val gender: String = "",
    val nationality: String = "",
    val from: String = "Unknown",
    val to: String = "Unknown"
)
