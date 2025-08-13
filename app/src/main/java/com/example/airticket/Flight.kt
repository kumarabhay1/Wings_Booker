package com.example.airticket

import java.io.Serializable


data class Flight(
    val airline: String,
    val flight_number: String,
    val departure_time: String,
    val arrival_time: String,
    val departure_airport: String,
    val arrival_airport: String,
    val duration: String,
    val price: String
) : Serializable
