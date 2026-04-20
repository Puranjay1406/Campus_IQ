package com.example.campusiq.ui.expense

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.campusiq.R
import com.example.campusiq.data.DatabaseHelper
import com.example.campusiq.data.models.Expense
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var etAmount: EditText
    private lateinit var etDesc: EditText
    private lateinit var spinner: Spinner
    private lateinit var chkImpulsive: CheckBox

    private val categories = listOf(
        "Food", "Transport", "Stationery", "Entertainment",
        "Personal Care", "Medical", "Other"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        db = DatabaseHelper(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        etAmount     = findViewById(R.id.etAmount)
        etDesc       = findViewById(R.id.etDescription)
        spinner      = findViewById(R.id.spinnerCategory)
        chkImpulsive = findViewById(R.id.checkImpulsive)

        spinner.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, categories
        )

        findViewById<Button>(R.id.btnSave).setOnClickListener { save() }
    }

    private fun save() {
        val amtStr = etAmount.text.toString().trim()
        if (amtStr.isEmpty()) { etAmount.error = "Amount required"; return }
        val amt = amtStr.toDoubleOrNull()
        if (amt == null || amt <= 0) { etAmount.error = "Enter valid amount"; return }

        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val result = db.insertExpense(Expense(
            amount      = amt,
            category    = spinner.selectedItem.toString(),
            description = etDesc.text.toString().trim(),
            date        = date,
            isImpulsive = chkImpulsive.isChecked
        ))
        if (result != -1L) {
            Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
        }
    }
}