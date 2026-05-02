package com.example.campusiq.ui.shopping

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.campusiq.R
import com.example.campusiq.data.FirestoreHelper
import com.example.campusiq.data.models.ShoppingItem
import java.text.SimpleDateFormat
import java.util.*

class AddShoppingActivity : AppCompatActivity() {

    private lateinit var fs: FirestoreHelper
    private lateinit var etName: EditText
    private lateinit var etAmount: EditText
    private lateinit var spinner: Spinner
    private lateinit var radioGroup: RadioGroup

    private val categories = listOf(
        "Stationery", "Clothing", "Electronics",
        "Personal Care", "Books", "Food", "Other"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_shopping)
        window.statusBarColor = android.graphics.Color.parseColor("#1A1A2E")
        window.navigationBarColor = android.graphics.Color.parseColor("#F4F6FB")

        fs = FirestoreHelper()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        etName     = findViewById(R.id.etItemName)
        etAmount   = findViewById(R.id.etAmount)
        spinner    = findViewById(R.id.spinnerCategory)
        radioGroup = findViewById(R.id.radioGroupPlanned)

        spinner.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, categories
        )

        findViewById<Button>(R.id.btnSave).setOnClickListener { save() }
    }

    private fun save() {
        val name   = etName.text.toString().trim()
        val amtStr = etAmount.text.toString().trim()
        if (name.isEmpty())   { etName.error = "Item name required"; return }
        if (amtStr.isEmpty()) { etAmount.error = "Amount required"; return }
        val amt = amtStr.toDoubleOrNull()
        if (amt == null || amt <= 0) { etAmount.error = "Enter valid amount"; return }

        val isPlanned = radioGroup.checkedRadioButtonId == R.id.rbPlanned
        val date      = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        fs.insertShoppingItem(ShoppingItem(
            itemName  = name, amount = amt,
            category  = spinner.selectedItem.toString(),
            isPlanned = isPlanned, date = date
        )) { success ->
            runOnUiThread {
                if (success) {
                    Toast.makeText(this, "Item saved!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}