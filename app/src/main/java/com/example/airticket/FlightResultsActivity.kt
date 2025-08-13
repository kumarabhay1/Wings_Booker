package com.example.airticket

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.airticket.databinding.ActivityFlightResultsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FlightResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlightResultsBinding
    private lateinit var flightAdapter: FlightAdapter

    private lateinit var from: String
    private lateinit var to: String
    private lateinit var date: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlightResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        from = intent.getStringExtra("from") ?: ""
        to = intent.getStringExtra("to") ?: ""
        date = intent.getStringExtra("date") ?: ""

        if (from.isEmpty() || to.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Invalid flight search parameters", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        fetchFlights(from, to)
    }

    private fun fetchFlights(from: String, to: String) {
        binding.progressBar.visibility = View.VISIBLE

        RetroFitClient.instance.searchFlights(from, to).enqueue(object : Callback<List<Flight>> {
            override fun onResponse(call: Call<List<Flight>>, response: Response<List<Flight>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val flights = response.body()!!
                    flightAdapter = FlightAdapter(flights) { selectedFlight ->
                        val intent = Intent(this@FlightResultsActivity, PassengerDetailsActivity::class.java).apply {
                            putExtra("flight", selectedFlight) // If needed, else remove this line
                            putExtra("from", from)  // Pass from city
                            putExtra("to", to)      // Pass to city
                        }
                        startActivity(intent)
                    }
                    binding.recyclerView.adapter = flightAdapter
                } else {
                    Toast.makeText(this@FlightResultsActivity, "No flights found.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Flight>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@FlightResultsActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
