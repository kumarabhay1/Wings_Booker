package com.example.airticket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FlightAdapter(
    private val flightList: List<Flight>,
    private val onBookClick: (Flight) -> Unit
) : RecyclerView.Adapter<FlightAdapter.FlightViewHolder>() {

    inner class FlightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtAirline: TextView = itemView.findViewById(R.id.txtAirline)
        val txtRoute: TextView = itemView.findViewById(R.id.txtRoute)
        val txtDeparture: TextView = itemView.findViewById(R.id.txtDeparture)
        val txtArrival: TextView = itemView.findViewById(R.id.txtArrival)
        val txtDuration: TextView = itemView.findViewById(R.id.txtDuration)
        val txtPrice: TextView = itemView.findViewById(R.id.txtPrice)
        val btnBookNow: Button = itemView.findViewById(R.id.btnBookNow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_flight, parent, false)
        return FlightViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        val flight = flightList[position]
        holder.txtAirline.text = flight.airline
        holder.txtRoute.text = "${flight.departure_airport} → ${flight.arrival_airport}"
        holder.txtDeparture.text = flight.departure_time.substringAfter("T").substring(0, 5)
        holder.txtArrival.text = flight.arrival_time.substringAfter("T").substring(0, 5)
        holder.txtDuration.text = flight.duration
        holder.txtPrice.text = flight.price

        holder.btnBookNow.setOnClickListener {
            onBookClick(flight)
        }
    }

    override fun getItemCount(): Int = flightList.size
}
