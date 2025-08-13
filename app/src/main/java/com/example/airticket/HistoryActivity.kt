package com.example.airticket

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookingAdapter: HistoryAdapter
    private val bookingsList = mutableListOf<Booking>()

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)  // Your layout file

        recyclerView = findViewById(R.id.historyRecyclerView) // RecyclerView in your XML
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize adapter with a cancel callback
        bookingAdapter = HistoryAdapter(this, bookingsList) { bookingToCancel ->
            cancelBooking(bookingToCancel)
        }
        recyclerView.adapter = bookingAdapter

        loadBookings()
    }

    private fun loadBookings() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }
        val userId = user.uid

        firestore.collection("users")
            .document(userId)
            .collection("bookings")
            .get()
            .addOnSuccessListener { snapshot ->
                bookingsList.clear()
                for (doc in snapshot.documents) {
                    val booking = doc.toObject(Booking::class.java)
                    if (booking != null) {
                        // Keep Firestore doc ID in the booking object for deletion
                        val bookingWithId = booking.copy(id = doc.id)
                        bookingsList.add(bookingWithId)
                    }
                }
                bookingAdapter.updateList(bookingsList)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load bookings", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cancelBooking(booking: Booking) {
        val user = auth.currentUser ?: return
        val userId = user.uid

        firestore.collection("users")
            .document(userId)
            .collection("bookings")
            .document(booking.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Booking cancelled", Toast.LENGTH_SHORT).show()
                // Remove booking locally and update adapter
                bookingsList.remove(booking)
                bookingAdapter.updateList(bookingsList)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to cancel booking", Toast.LENGTH_SHORT).show()
            }
    }
}
