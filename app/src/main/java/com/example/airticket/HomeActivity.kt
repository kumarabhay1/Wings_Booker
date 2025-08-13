package com.example.airticket

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.airticket.databinding.ActivityHomeBinding
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val calendar = Calendar.getInstance()
    private val airportList = mutableListOf<Airport>()
    private val suggestionList = mutableListOf<String>()
    private val iataMap = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set default selected item
        binding.bottomNavigation.selectedItemId = R.id.nav_home

        // Handle navigation item clicks
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> true // Already on Home
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }

        loadAirports()
        setupAutoComplete()
        setupDatePicker()
        setupSearchButton()
    }

    private fun setupAutoComplete() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestionList)
        (binding.etFrom as? AutoCompleteTextView)?.setAdapter(adapter)
        (binding.etTo as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    binding.etDate.setText(sdf.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupSearchButton() {
        binding.btnSearch.setOnClickListener {
            val fromText = binding.etFrom.text.toString().trim()
            val toText = binding.etTo.text.toString().trim()
            val date = binding.etDate.text.toString().trim()
            val isRoundTrip = binding.switchTripType.isChecked

            val fromIATA = iataMap[fromText.lowercase()] ?: ""
            val toIATA = iataMap[toText.lowercase()] ?: ""

            if (fromIATA.isEmpty() || toIATA.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill all fields with valid city names or airport codes.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, FlightResultsActivity::class.java).apply {
                putExtra("from", fromIATA)
                putExtra("to", toIATA)
                putExtra("date", date)
                putExtra("roundTrip", isRoundTrip)
            }
            startActivity(intent)
        }
    }

    private fun loadAirports() {
        val inputStream = assets.open("airports.json")
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val json = bufferedReader.readText()
        val jsonArray = JSONArray(json)

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val code = obj.getString("code")
            val name = obj.getString("name")
            val city = obj.getString("city")
            val country = obj.getString("country")

            val airport = Airport(code, name, city, country)
            airportList.add(airport)

            val displayText = "$city ($code) - $name"
            suggestionList.add(displayText)

            iataMap[city.lowercase()] = code
            iataMap[code.lowercase()] = code
            iataMap[name.lowercase()] = code
            iataMap[displayText.lowercase()] = code
        }
    }
}
