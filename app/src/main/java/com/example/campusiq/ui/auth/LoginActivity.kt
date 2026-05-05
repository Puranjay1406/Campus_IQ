package com.example.campusiq.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.campusiq.R
import com.example.campusiq.data.FirestoreHelper
import com.example.campusiq.utils.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.example.campusiq.ui.HomeActivity
import com.example.campusiq.ui.BaseActivity

class LoginActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvSignup: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        enableImmersiveMode()

        window.statusBarColor = android.graphics.Color.parseColor("#1A1A2E")
        window.navigationBarColor = android.graphics.Color.parseColor("#F4F6FB")

        auth        = FirebaseAuth.getInstance()
        etEmail     = findViewById(R.id.etEmail)
        etPassword  = findViewById(R.id.etPassword)
        btnLogin    = findViewById(R.id.btnLogin)
        tvSignup    = findViewById(R.id.tvSignup)
        progressBar = findViewById(R.id.progressBar)

        btnLogin.setOnClickListener { login() }
        tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun login() {
        val email    = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty())     { etEmail.error = "Email required"; return }
        if (password.isEmpty())  { etPassword.error = "Password required"; return }
        if (password.length < 6) { etPassword.error = "Min 6 characters"; return }

        setLoading(true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                setLoading(false)
                val fs    = FirestoreHelper()
                val prefs = PreferenceManager(this)

                fs.loadUserProfile { data: Map<String, Any>? ->
                    if (data != null) {
                        prefs.studentName   = data["studentName"] as? String ?: ""
                        prefs.monthlyBudget = (data["monthlyBudget"] as? Double)
                            ?.toFloat() ?: 5000f
                        prefs.hostelName    = data["hostelName"] as? String ?: ""
                        prefs.semester      = (data["semester"] as? Long)
                            ?.toInt() ?: 1
                    }
                    runOnUiThread {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                }
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, e.message ?: "Login failed", Toast.LENGTH_LONG).show()
            }
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnLogin.isEnabled     = !loading
        btnLogin.text          = if (loading) "Logging in..." else "Login"
    }
}