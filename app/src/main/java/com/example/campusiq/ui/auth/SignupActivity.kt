package com.example.campusiq.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.campusiq.R
import com.example.campusiq.ui.OnboardingActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.campusiq.ui.BaseActivity

class SignupActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirm: EditText
    private lateinit var btnSignup: Button
    private lateinit var tvLogin: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        enableImmersiveMode()

        window.statusBarColor = android.graphics.Color.parseColor("#1A1A2E")
        window.navigationBarColor = android.graphics.Color.parseColor("#F4F6FB")

        auth        = FirebaseAuth.getInstance()
        etName      = findViewById(R.id.etName)
        etEmail     = findViewById(R.id.etEmail)
        etPassword  = findViewById(R.id.etPassword)
        etConfirm   = findViewById(R.id.etConfirmPassword)
        btnSignup   = findViewById(R.id.btnSignup)
        tvLogin     = findViewById(R.id.tvLogin)
        progressBar = findViewById(R.id.progressBar)

        btnSignup.setOnClickListener { signup() }
        tvLogin.setOnClickListener { finish() }
    }

    private fun signup() {
        val name     = etName.text.toString().trim()
        val email    = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirm  = etConfirm.text.toString().trim()

        if (name.isEmpty())     { etName.error = "Name required"; return }
        if (email.isEmpty())    { etEmail.error = "Email required"; return }
        if (password.isEmpty()) { etPassword.error = "Password required"; return }
        if (password.length < 6) { etPassword.error = "Min 6 characters"; return }
        if (password != confirm) { etConfirm.error = "Passwords don't match"; return }

        setLoading(true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                setLoading(false)
                // Update display name in Firebase
                val profileUpdate = com.google.firebase.auth.UserProfileChangeRequest
                    .Builder()
                    .setDisplayName(name)
                    .build()
                auth.currentUser?.updateProfile(profileUpdate)

                // Go to onboarding
                startActivity(Intent(this, OnboardingActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, e.message ?: "Signup failed", Toast.LENGTH_LONG).show()
            }
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnSignup.isEnabled    = !loading
        btnSignup.text         = if (loading) "Creating account..." else "Create Account"
    }
}