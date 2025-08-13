package com.example.airticket.Utils
// utils/AirportUtils.kt


import android.content.Context
import com.example.airticket.Airport
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object AirportUtils {
    fun loadAirportsFromAssets(context: Context): List<Airport> {
        val json = context.assets.open("airports.json").bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<Airport>>() {}.type
        return Gson().fromJson(json, listType)
    }
}
