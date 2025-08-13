package com.example.airticket

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONArray


class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var fullNameEditText: TextInputEditText
    private lateinit var ageEditText: TextInputEditText
    private lateinit var genderEditText: AutoCompleteTextView
    private lateinit var nationalityEditText: AutoCompleteTextView
    private lateinit var phoneEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var signUpButton: Button
    private lateinit var goToLoginTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize views
        fullNameEditText = findViewById(R.id.etSignupFullName)
        ageEditText = findViewById(R.id.etSignupAge)
        genderEditText = findViewById(R.id.etSignupGender)
        nationalityEditText = findViewById(R.id.etSignupNationality)
        phoneEditText = findViewById(R.id.etSignupPhone)
        emailEditText = findViewById(R.id.etSignupEmail)
        passwordEditText = findViewById(R.id.etSignupPassword)
        confirmPasswordEditText = findViewById(R.id.etSignupConfirmPassword)
        signUpButton = findViewById(R.id.btnSignup)
        goToLoginTextView = findViewById(R.id.tvGoToLogin)

        // Gender Dropdown
        val genderOptions = listOf("Male", "Female", "Other")
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genderOptions)
        val genderAutoComplete = findViewById<AutoCompleteTextView>(R.id.etSignupGender)
        genderAutoComplete.setAdapter(genderAdapter)
        genderAutoComplete.setOnClickListener { genderAutoComplete.showDropDown() }

        // Nationality Dropdown (Countries from JSON)
        val countries = loadCountriesFromAssets()
        val nationalityAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, countries)
        val nationalityAutoComplete = findViewById<AutoCompleteTextView>(R.id.etSignupNationality)
        nationalityAutoComplete.setAdapter(nationalityAdapter)
        nationalityAutoComplete.setOnClickListener { nationalityAutoComplete.showDropDown() }


        // Sign up button click listener
        signUpButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString().trim()
            val age = ageEditText.text.toString().trim()
            val gender = genderEditText.text.toString().trim()
            val nationality = nationalityEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (validateInput(fullName, age, gender, nationality, phone, email, password, confirmPassword)) {
                createAccount(email, password, fullName, age, gender, nationality, phone)
            }
        }

        // Go to Login activity
        goToLoginTextView.setOnClickListener {
            // Navigate to Login Activity (you can implement Intent here)
        }
    }

    // Validate user input
    private fun validateInput(
        fullName: String, age: String, gender: String, nationality: String,
        phone: String, email: String, password: String, confirmPassword: String
    ): Boolean {
        return when {
            TextUtils.isEmpty(fullName) -> {
                showToast("Full Name is required")
                false
            }
            TextUtils.isEmpty(age) -> {
                showToast("Age is required")
                false
            }
            TextUtils.isEmpty(gender) -> {
                showToast("Gender is required")
                false
            }
            TextUtils.isEmpty(nationality) -> {
                showToast("Nationality is required")
                false
            }
            TextUtils.isEmpty(phone) -> {
                showToast("Phone number is required")
                false
            }
            TextUtils.isEmpty(email) -> {
                showToast("Email is required")
                false
            }
            TextUtils.isEmpty(password) -> {
                showToast("Password is required")
                false
            }
            password != confirmPassword -> {
                showToast("Passwords do not match")
                false
            }
            else -> true
        }
    }

    // Create user account with Firebase Authentication and Firestore
    private fun createAccount(email: String, password: String, fullName: String, age: String, gender: String, nationality: String, phone: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid

                    // Create a user data map
                    val userMap = hashMapOf(
                        "fullName" to fullName,
                        "age" to age,
                        "gender" to gender,
                        "nationality" to nationality,
                        "phone" to phone,
                        "email" to email
                    )

                    // Store user data in Firestore
                    userId?.let {
                        firestore.collection("users")
                            .document(it)  // Use the user ID as the document ID
                            .set(userMap)
                            .addOnSuccessListener {
                                showToast("Account created successfully")
                                // Navigate to the home screen or another activity
                                val intent = Intent(this, HomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                showToast("Failed to save user details: ${e.message}")
                            }
                    }
                } else {
                    showToast("Authentication failed: ${task.exception?.message}")
                }
            }
    }

    // Show toast message
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun loadCountriesFromAssets(): List<String> {
        val jsonString = assets.open("countries.json").bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)
        val countryList = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            countryList.add(jsonArray.getString(i))
        }
        return countryList
    }
}
