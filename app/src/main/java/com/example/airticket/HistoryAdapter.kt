package com.example.airticket

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast

class HistoryAdapter(
    private val context: Context,
    private var bookings: MutableList<Booking>,
    private val onBookingCancelled: (Booking) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.BookingViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.bookingName)
        val email: TextView = itemView.findViewById(R.id.bookingEmail)
        val phone: TextView = itemView.findViewById(R.id.bookingPhone)
        val from: TextView = itemView.findViewById(R.id.bookingFrom)
        val to: TextView = itemView.findViewById(R.id.bookingTo)
        val nationality: TextView = itemView.findViewById(R.id.bookingNationality)
        val btnCancel: Button = itemView.findViewById(R.id.btnCancelBooking)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_booking_history, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]

        holder.name.text = booking.fullName
        holder.email.text = "Email: ${booking.email}"
        holder.phone.text = "Phone: ${booking.phone}"
        holder.from.text = "From: ${booking.from}"
        holder.to.text = "To: ${booking.to}"
        holder.nationality.text = "Nationality: ${booking.nationality}"

        holder.btnCancel.setOnClickListener {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = currentUser.uid

            // Delete booking document from Firestore
            firestore.collection("users")
                .document(userId)
                .collection("bookings")
                .document(booking.id)
                .delete()
                .addOnSuccessListener {
                    // Remove item locally and notify adapter
                    bookings.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, bookings.size)
                    Toast.makeText(context, "Booking cancelled successfully", Toast.LENGTH_SHORT).show()
                    onBookingCancelled(booking)
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to cancel booking", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun getItemCount(): Int = bookings.size

    fun updateList(newList: MutableList<Booking>) {
        bookings = newList
        notifyDataSetChanged()
    }
}
