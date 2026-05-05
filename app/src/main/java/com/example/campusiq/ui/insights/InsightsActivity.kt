package com.example.campusiq.ui.insights

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.campusiq.R
import com.example.campusiq.data.FirestoreHelper
import com.example.campusiq.utils.PreferenceManager

class InsightsActivity : AppCompatActivity() {

    private lateinit var fs: FirestoreHelper
    private lateinit var prefs: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insights)

        window.statusBarColor = android.graphics.Color.parseColor("#1A1A2E")
        window.navigationBarColor = android.graphics.Color.parseColor("#F4F6FB")

        fs    = FirestoreHelper()
        prefs = PreferenceManager(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        loadInsights()
    }

    private fun loadInsights() {
        val budget = prefs.monthlyBudget.toDouble()

        fs.getAllExpenses { expenses ->
            fs.getTotalExpense { expTotal ->
                fs.getTotalFoodCost { foodTotal ->
                    fs.getAllShoppingItems { shopping ->
                        runOnUiThread {
                            val totalSpent = expTotal + foodTotal
                            val pct = if (budget > 0)
                                (totalSpent / budget * 100).toInt().coerceAtMost(100) else 0

                            // Budget
                            findViewById<TextView>(R.id.tvTotalSpent).text =
                                "Rs." + String.format("%.2f", totalSpent)
                            findViewById<ProgressBar>(R.id.progressBudget).progress = pct
                            findViewById<TextView>(R.id.tvBudgetStatus).text = when {
                                pct < 50  -> "Well within budget. Great discipline!"
                                pct < 80  -> "Budget usage getting high. Slow down."
                                pct < 100 -> "Almost at limit! Control spending now."
                                else      -> "Over budget! Stop non-essential spending."
                            }

                            // Top category
                            fs.getExpenseByCategory { catMap ->
                                runOnUiThread {
                                    val top = catMap.maxByOrNull { it.value }
                                    findViewById<TextView>(R.id.tvTopCategory).text =
                                        top?.let {
                                            it.key + " — Rs." +
                                                    String.format("%.2f", it.value)
                                        } ?: "No data yet"
                                }
                            }

                            // Shopping insights
                            val impCount  = shopping.count { !it.isPlanned }
                            val planCount = shopping.count { it.isPlanned }
                            val impAmt    = shopping.filter { !it.isPlanned }.sumOf { it.amount }
                            val impPct    = if (shopping.isNotEmpty())
                                impCount * 100 / shopping.size else 0

                            findViewById<TextView>(R.id.tvShoppingInsight).text =
                                "Total items: ${shopping.size}\n" +
                                        "Impulsive: $impCount ($impPct%)\n" +
                                        "Planned: $planCount\n" +
                                        "Impulsive spend: Rs." +
                                        String.format("%.2f", impAmt)

                            // Food insights
                            fs.getAllFoodEntries { foodList ->
                                runOnUiThread {
                                    val outside = foodList
                                        .filter { it.location == "Outside" }
                                        .sumOf { it.cost }
                                    val mess = foodList
                                        .filter { it.location == "Hostel Mess" }
                                        .sumOf { it.cost }
                                    val outsidePct = if (foodTotal > 0)
                                        (outside / foodTotal * 100).toInt() else 0

                                    findViewById<TextView>(R.id.tvFoodInsight).text =
                                        "Total food spend: Rs." +
                                                String.format("%.2f", foodTotal) + "\n" +
                                                "Hostel Mess: Rs." +
                                                String.format("%.2f", mess) + "\n" +
                                                "Outside: Rs." +
                                                String.format("%.2f", outside) +
                                                " ($outsidePct%)"

                                    // Suggestions
                                    findViewById<TextView>(R.id.tvSuggestions).text =
                                        buildSuggestions(pct, outsidePct, impPct)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun buildSuggestions(
        budgetPct: Int,
        outsidePct: Int,
        impulsivePct: Int
    ): String {
        val sb = StringBuilder()
        if (budgetPct > 80)
            sb.appendLine("You have used $budgetPct% of your budget. Reduce discretionary spending.")
        if (outsidePct > 50)
            sb.appendLine("$outsidePct% of food spend is outside. Eating at the mess could save significantly.")
        if (impulsivePct > 40)
            sb.appendLine("$impulsivePct% of purchases are impulsive. Wait 24 hours before buying non-essentials.")
        if (sb.isEmpty())
            sb.appendLine("Keep logging to get personalized insights. You are on track!")
        return sb.toString().trimEnd()
    }
}