package com.example.airticket

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.airticket.databinding.ActivityPassengerDetailsBinding
import com.example.airticket.model.Passenger
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PassengerDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPassengerDetailsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPassengerDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        val fromLocation = intent.getStringExtra("from") ?: ""
        val toLocation = intent.getStringExtra("to") ?: ""

        binding.tvFrom.text = fromLocation
        binding.tvTo.text = toLocation
        // Load user data from Firestore
        loadUserData()

        binding.btnContinue.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val ageStr = binding.etAge.text.toString().trim()
            val nationality = binding.etNationality.text.toString().trim()
            val genderId = binding.genderGroup.checkedRadioButtonId



            if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || ageStr.isEmpty() ||
                nationality.isEmpty() || genderId == -1 || fromLocation.isEmpty() || toLocation.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (phone.length < 10) {
                Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val age = try {
                ageStr.toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Enter a valid age", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val gender = when (genderId) {
                R.id.radioMale -> "Male"
                R.id.radioFemale -> "Female"
                R.id.radioOther -> "Other"
                else -> "Unspecified"
            }

            // Create Passenger with from and to included
            val passenger = Passenger(
                fullName = fullName,
                email = email,
                phone = phone,
                age = ageStr,
                gender = gender,
                nationality = nationality,
                from = fromLocation,
                to = toLocation
            )

            // Pass Passenger Parcelable object to PaymentActivity
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("passenger", passenger)
            startActivity(intent)
        }
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    binding.etFullName.setText(document.getString("fullName") ?: "")
                    binding.etEmail.setText(document.getString("email") ?: "")
                    binding.etPhone.setText(document.getString("phone") ?: "")
                    binding.etAge.setText(document.getString("age") ?: "")
                    binding.etNationality.setText(document.getString("nationality") ?: "")

                    when (document.getString("gender")) {
                        "Male" -> binding.radioMale.isChecked = true
                        "Female" -> binding.radioFemale.isChecked = true
                        "Other" -> binding.radioOther.isChecked = true
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
            }
    }
}
