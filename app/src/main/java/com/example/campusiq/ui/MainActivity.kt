package com.example.campusiq.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.campusiq.R
import com.example.campusiq.data.FirestoreHelper
import com.example.campusiq.ui.expense.ExpenseActivity
import com.example.campusiq.ui.food.FoodActivity
import com.example.campusiq.ui.insights.InsightsActivity
import com.example.campusiq.ui.shopping.ShoppingActivity
import com.example.campusiq.utils.PreferenceManager

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.statusBarColor = android.graphics.Color.parseColor("#1A1A2E")
        window.navigationBarColor = android.graphics.Color.parseColor("#F4F6FB")

        prefs = PreferenceManager(this)

        // Profile icon
        findViewById<TextView>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Feature buttons
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
            "Rs." + String.format("%.0f", budget)

        val fs = FirestoreHelper()

        fs.getAllExpenses { expenses ->
            fs.getTotalExpense { expTotal ->
                fs.getTotalFoodCost { foodTotal ->
                    fs.getAllFoodEntries { foodList ->
                        fs.getAllShoppingItems { shopping ->
                            runOnUiThread {
                                val totalSpent = expTotal + foodTotal
                                val progress   = if (budget > 0)
                                    ((totalSpent / budget) * 100)
                                        .toInt()
                                        .coerceAtMost(100)
                                else 0

                                findViewById<ProgressBar>(R.id.progressBudget)
                                    .progress = progress

                                findViewById<TextView>(R.id.tvTotalSpent).text =
                                    "Rs." + String.format("%.0f", totalSpent) + " spent"

                                val remaining = budget - totalSpent
                                val remView   = findViewById<TextView>(R.id.tvRemaining)
                                remView.text  = if (remaining >= 0)
                                    "Rs." + String.format("%.0f", remaining) + " remaining"
                                else
                                    "Over budget by Rs." + String.format("%.0f", -remaining)
                                remView.setTextColor(
                                    if (remaining >= 0)
                                        getColor(android.R.color.holo_green_dark)
                                    else
                                        getColor(android.R.color.holo_red_dark)
                                )

                                val impPct = if (shopping.isNotEmpty())
                                    shopping.count { !it.isPlanned } * 100 / shopping.size
                                else 0

                                findViewById<TextView>(R.id.tvExpenseCount).text =
                                    expenses.size.toString()
                                findViewById<TextView>(R.id.tvFoodCount).text =
                                    foodList.size.toString()
                                findViewById<TextView>(R.id.tvShoppingCount).text =
                                    shopping.size.toString()
                                findViewById<TextView>(R.id.tvImpulsivePct).text =
                                    "$impPct%"
                            }
                        }
                    }
                }
            }
        }
    }
}