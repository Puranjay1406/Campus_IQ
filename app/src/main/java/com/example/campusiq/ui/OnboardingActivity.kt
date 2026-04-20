package com.example.campusiq.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.campusiq.R
import com.example.campusiq.utils.PreferenceManager

class OnboardingActivity : AppCompatActivity() {

    private lateinit var prefs: PreferenceManager
    private lateinit var etName: EditText
    private lateinit var etBudget: EditText
    private lateinit var etHostel: EditText
    private lateinit var spinnerSem: Spinner
    private lateinit var btnStart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        window.statusBarColor = android.graphics.Color.parseColor("#1A1A2E")
        window.navigationBarColor = android.graphics.Color.parseColor("#F4F6FB")

        prefs      = PreferenceManager(this)
        etName     = findViewById(R.id.etName)
        etBudget   = findViewById(R.id.etBudget)
        etHostel   = findViewById(R.id.etHostel)
        spinnerSem = findViewById(R.id.spinnerSemester)
        btnStart   = findViewById(R.id.btnGetStarted)

        spinnerSem.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            (1..8).map { "Semester $it" }
        )

        btnStart.setOnClickListener { saveAndProceed() }
    }

    private fun saveAndProceed() {
        val name      = etName.text.toString().trim()
        val budgetStr = etBudget.text.toString().trim()

        if (name.isEmpty())      { etName.error = "Name is required"; return }
        if (budgetStr.isEmpty()) { etBudget.error = "Budget is required"; return }

        val budget = budgetStr.toFloatOrNull()
        if (budget == null || budget <= 0f) {
            etBudget.error = "Enter a valid amount"
            return
        }

        prefs.studentName   = name
        prefs.monthlyBudget = budget
        prefs.hostelName    = etHostel.text.toString().trim()
        prefs.semester      = spinnerSem.selectedItemPosition + 1
        prefs.isOnboarded   = true

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}