package com.example.airticket

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.airticket.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        loadProfileData()

        binding.btnSave.setOnClickListener {
            updateProfileInFirestore()
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadProfileData() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    binding.etName.setText(document.getString("fullName"))
                    binding.etEmail.setText(document.getString("email"))
                    binding.etPhone.setText(document.getString("phone"))
                    binding.etAge.setText(document.getString("age"))
                    binding.etGender.setText(document.getString("gender"))
                    binding.etNationality.setText(document.getString("nationality"))
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateProfileInFirestore() {
        val userId = auth.currentUser?.uid ?: return

        val profileUpdates = hashMapOf(
            "fullName" to binding.etName.text.toString(),
            "email" to binding.etEmail.text.toString(),
            "phone" to binding.etPhone.text.toString(),
            "age" to binding.etAge.text.toString(),
            "gender" to binding.etGender.text.toString(),
            "nationality" to binding.etNationality.text.toString()
        )

        firestore.collection("users")
            .document(userId)
            .set(profileUpdates)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }
}
