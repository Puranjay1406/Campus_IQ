package com.example.campusiq.ui.food

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.campusiq.R
import com.example.campusiq.data.DatabaseHelper
import com.example.campusiq.data.models.FoodEntry
import java.text.SimpleDateFormat
import java.util.*

class AddFoodActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var etItem: EditText
    private lateinit var etCost: EditText
    private lateinit var spinnerMeal: Spinner
    private lateinit var spinnerLoc: Spinner
    private lateinit var spinnerMood: Spinner

    private val meals  = listOf("Breakfast", "Lunch", "Dinner", "Snack")
    private val locs   = listOf("Hostel Mess", "Canteen", "Outside", "Self Cooked")
    private val moods  = listOf("Normal", "Happy", "Stressed", "Bored", "Hungry")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)
        db = DatabaseHelper(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        etItem       = findViewById(R.id.etFoodItem)
        etCost       = findViewById(R.id.etCost)
        spinnerMeal  = findViewById(R.id.spinnerMealType)
        spinnerLoc   = findViewById(R.id.spinnerLocation)
        spinnerMood  = findViewById(R.id.spinnerMood)

        spinnerMeal.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, meals)
        spinnerLoc.adapter  = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, locs)
        spinnerMood.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, moods)

        findViewById<Button>(R.id.btnSave).setOnClickListener { save() }
    }

    private fun save() {
        val item = etItem.text.toString().trim()
        val costStr = etCost.text.toString().trim()
        if (item.isEmpty())    { etItem.error = "Food item required"; return }
        if (costStr.isEmpty()) { etCost.error = "Cost required"; return }
        val cost = costStr.toDoubleOrNull()
        if (cost == null || cost < 0) { etCost.error = "Enter valid cost"; return }

        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val result = db.insertFoodEntry(FoodEntry(
            mealType = spinnerMeal.selectedItem.toString(),
            foodItem = item, cost = cost,
            location = spinnerLoc.selectedItem.toString(),
            date = date,
            mood = spinnerMood.selectedItem.toString()
        ))
        if (result != -1L) { Toast.makeText(this, "Food entry saved!", Toast.LENGTH_SHORT).show(); finish() }
        else Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
    }
}