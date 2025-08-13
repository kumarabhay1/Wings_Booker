package com.example.airticket

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.airticket.databinding.ActivityPaymentBinding
import com.example.airticket.model.Passenger
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private lateinit var passenger: Passenger
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get passenger object from intent
        passenger = intent.getParcelableExtra("passenger")!!

        // Display passenger details including from and to
        binding.tvPassengerSummary.text = """
            Name: ${passenger.fullName}
            Email: ${passenger.email}
            Phone: ${passenger.phone}
            Age: ${passenger.age}
            Gender: ${passenger.gender}
            Nationality: ${passenger.nationality}
            From: ${passenger.from}
            To: ${passenger.to}
        """.trimIndent()

        // Handle payment button click
        binding.btnPayNow.setOnClickListener {
            saveBookingToFirebase(passenger)
        }
    }

    private fun saveBookingToFirebase(passenger: Passenger) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid

        val bookingData = hashMapOf(
            "fullName" to passenger.fullName,
            "email" to passenger.email,
            "phone" to passenger.phone,
            "age" to passenger.age,
            "gender" to passenger.gender,
            "nationality" to passenger.nationality,
            "from" to passenger.from,     // Added from
            "to" to passenger.to,         // Added to
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("users")
            .document(userId)
            .collection("bookings")
            .add(bookingData)
            .addOnSuccessListener {
                Toast.makeText(this, "Booking successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, BookingConfirmationActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save booking", Toast.LENGTH_SHORT).show()
            }
    }
}
