package com.example.campusiq.ui

import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.example.campusiq.R
import com.example.campusiq.ui.auth.LoginActivity
import com.example.campusiq.utils.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.example.campusiq.data.FirestoreHelper

class ProfileActivity : BaseActivity() {

    private lateinit var prefs: PreferenceManager
    private lateinit var etName: EditText
    private lateinit var etBudget: EditText
    private lateinit var etHostel: EditText
    private lateinit var spinnerSem: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnLogout: Button
    private lateinit var tvEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setStatusBarColor("#1A1A2E")
        setNavBarColor("#F4F6FB")
        applySystemBarInsets(R.id.rootProfile)

        window.statusBarColor = android.graphics.Color.parseColor("#1A1A2E")
        window.navigationBarColor = android.graphics.Color.parseColor("#F4F6FB")

        prefs = PreferenceManager(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        etName     = findViewById(R.id.etName)
        etBudget   = findViewById(R.id.etBudget)
        etHostel   = findViewById(R.id.etHostel)
        spinnerSem = findViewById(R.id.spinnerSemester)
        btnSave    = findViewById(R.id.btnSave)
        btnLogout  = findViewById(R.id.btnLogout)
        tvEmail    = findViewById(R.id.tvEmail)

        spinnerSem.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            (1..8).map { "Semester $it" }
        )

        loadCurrentValues()

        btnSave.setOnClickListener { saveProfile() }
        btnLogout.setOnClickListener { confirmLogout() }
    }

    private fun loadCurrentValues() {
        etName.setText(prefs.studentName)
        etBudget.setText(prefs.monthlyBudget.toInt().toString())
        etHostel.setText(prefs.hostelName)
        spinnerSem.setSelection((prefs.semester - 1).coerceAtLeast(0))
        val user = FirebaseAuth.getInstance().currentUser
        tvEmail.text = user?.email ?: "No email found"
    }

    private fun saveProfile() {
        val name      = etName.text.toString().trim()
        val budgetStr = etBudget.text.toString().trim()

        if (name.isEmpty())      { etName.error = "Name required"; return }
        if (budgetStr.isEmpty()) { etBudget.error = "Budget required"; return }

        val budget = budgetStr.toFloatOrNull()
        if (budget == null || budget <= 0f) {
            etBudget.error = "Enter valid amount"
            return
        }

        val hostel   = etHostel.text.toString().trim()
        val semester = spinnerSem.selectedItemPosition + 1

        // Save to SharedPreferences
        prefs.studentName   = name
        prefs.monthlyBudget = budget
        prefs.hostelName    = hostel
        prefs.semester      = semester

        // Save to Firestore
        val fs = FirestoreHelper()
        fs.saveUserProfile(name, budget, hostel, semester) { success ->
            runOnUiThread {
                if (success) {
                    Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Saved locally, cloud sync failed", Toast.LENGTH_SHORT).show()
                }
                finish()
            }
        }
    }

    private fun confirmLogout() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}