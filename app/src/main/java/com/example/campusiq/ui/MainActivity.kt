package com.example.campusiq.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.campusiq.R
import com.example.campusiq.data.DatabaseHelper
import com.example.campusiq.ui.expense.ExpenseActivity
import com.example.campusiq.ui.food.FoodActivity
import com.example.campusiq.ui.insights.InsightsActivity
import com.example.campusiq.ui.shopping.ShoppingActivity
import com.example.campusiq.utils.PreferenceManager
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var prefs: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db    = DatabaseHelper(this)
        prefs = PreferenceManager(this)

        findViewById<Button>(R.id.btnExpenses).setOnClickListener {
            startActivity(Intent(this, ExpenseActivity::class.java))
        }
        findViewById<Button>(R.id.btnFood).setOnClickListener {
            startActivity(Intent(this, FoodActivity::class.java))
        }
        findViewById<Button>(R.id.btnShopping).setOnClickListener {
            startActivity(Intent(this, ShoppingActivity::class.java))
        }
        findViewById<Button>(R.id.btnInsights).setOnClickListener {
            startActivity(Intent(this, InsightsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadDashboard()
    }

    private fun loadDashboard() {
        val name   = prefs.studentName.ifEmpty { "Student" }
        val budget = prefs.monthlyBudget.toDouble()

        findViewById<TextView>(R.id.tvGreeting).text = "Hello, $name!"
        findViewById<TextView>(R.id.tvBudgetLabel).text =
            "Budget: Rs." + String.format("%.0f", budget)

        val totalSpent = db.getTotalExpense() + db.getTotalFoodCost()
        val progress   = if (budget > 0)
            ((totalSpent / budget) * 100).roundToInt().coerceAtMost(100) else 0

        findViewById<ProgressBar>(R.id.progressBudget).progress = progress
        findViewById<TextView>(R.id.tvTotalSpent).text =
            "Spent: Rs." + String.format("%.0f", totalSpent)

        val remaining  = budget - totalSpent
        val remView    = findViewById<TextView>(R.id.tvRemaining)
        remView.text   = if (remaining >= 0)
            "Rs." + String.format("%.0f", remaining) + " remaining"
        else "Over budget by Rs." + String.format("%.0f", -remaining)
        remView.setTextColor(
            if (remaining >= 0) getColor(android.R.color.holo_green_dark)
            else getColor(android.R.color.holo_red_dark)
        )

        val shopping       = db.getAllShoppingItems()
        val impulsiveCount = shopping.count { !it.isPlanned }
        val impPct         = if (shopping.isNotEmpty())
            impulsiveCount * 100 / shopping.size else 0

        findViewById<TextView>(R.id.tvExpenseCount).text  = db.getAllExpenses().size.toString()
        findViewById<TextView>(R.id.tvFoodCount).text     = db.getAllFoodEntries().size.toString()
        findViewById<TextView>(R.id.tvShoppingCount).text = shopping.size.toString()
        findViewById<TextView>(R.id.tvImpulsivePct).text  = "$impPct%"

        findViewById<Button>(R.id.btnReset).setOnClickListener {
            // Clear SharedPreferences
            prefs.isOnboarded = false
            prefs.studentName = ""
            prefs.monthlyBudget = 5000f
            prefs.hostelName = ""
            prefs.semester = 1

            // Clear all SQLite tables
            val dbHelper = DatabaseHelper(this)
            dbHelper.writableDatabase.execSQL("DELETE FROM expenses")
            dbHelper.writableDatabase.execSQL("DELETE FROM food_entries")
            dbHelper.writableDatabase.execSQL("DELETE FROM shopping_items")

            // Go back to onboarding
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
        }
    }
}