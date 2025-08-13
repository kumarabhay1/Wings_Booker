package com.example.airticket

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("flights")
    fun searchFlights(
        @Query("from") from: String,
        @Query("to") to: String
    ): Call<List<Flight>>

}
