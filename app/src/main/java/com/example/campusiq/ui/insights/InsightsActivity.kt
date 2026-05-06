package com.example.campusiq.ui.insights

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.campusiq.R
import com.example.campusiq.data.AIService
import com.example.campusiq.data.FirestoreHelper
import com.example.campusiq.ui.BaseActivity
import com.example.campusiq.utils.PreferenceManager
import kotlinx.coroutines.launch

class InsightsActivity : BaseActivity() {

    private lateinit var fs: FirestoreHelper
    private lateinit var prefs: PreferenceManager

    private lateinit var tvAISummary: TextView
    private lateinit var progressAI: ProgressBar
    private lateinit var tvTotalSpent: TextView
    private lateinit var progressBudget: ProgressBar
    private lateinit var tvBudgetStatus: TextView
    private lateinit var tvTopCategory: TextView
    private lateinit var tvShoppingInsight: TextView
    private lateinit var tvFoodInsight: TextView
    private lateinit var tvSuggestions: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insights)
        setStatusBarColor("#1A1A2E")
        setNavBarColor("#F4F6FB")
        applySystemBarInsets(R.id.rootInsights)

        fs    = FirestoreHelper()
        prefs = PreferenceManager(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        tvAISummary    = findViewById(R.id.tvAISummary)
        progressAI     = findViewById(R.id.progressAI)
        tvTotalSpent   = findViewById(R.id.tvTotalSpent)
        progressBudget = findViewById(R.id.progressBudget)
        tvBudgetStatus = findViewById(R.id.tvBudgetStatus)
        tvTopCategory  = findViewById(R.id.tvTopCategory)
        tvShoppingInsight = findViewById(R.id.tvShoppingInsight)
        tvFoodInsight  = findViewById(R.id.tvFoodInsight)
        tvSuggestions  = findViewById(R.id.tvSuggestions)

        loadInsights()
    }

    private fun loadInsights() {
        val budget = prefs.monthlyBudget.toDouble()

        fs.getAllExpenses { expenses ->
            fs.getTotalExpense { expTotal ->
                fs.getTotalFoodCost { foodTotal ->
                    fs.getAllShoppingItems { shopping ->
                        fs.getAllFoodEntries { foodList ->
                            runOnUiThread {
                                val totalSpent = expTotal + foodTotal
                                val pct = if (budget > 0)
                                    (totalSpent / budget * 100)
                                        .toInt().coerceAtMost(100) else 0

                                // Budget
                                tvTotalSpent.text =
                                    "Rs." + String.format("%.2f", totalSpent)
                                progressBudget.progress = pct
                                tvBudgetStatus.text = when {
                                    pct < 50  -> "Well within budget. Great discipline!"
                                    pct < 80  -> "Budget usage getting high. Slow down."
                                    pct < 100 -> "Almost at limit! Control spending now."
                                    else      -> "Over budget! Stop non-essential spending."
                                }

                                // Top category
                                fs.getExpenseByCategory { catMap ->
                                    runOnUiThread {
                                        val top = catMap.maxByOrNull { it.value }
                                        tvTopCategory.text = top?.let {
                                            it.key + " — Rs." +
                                                    String.format("%.2f", it.value)
                                        } ?: "No data yet"
                                    }
                                }

                                // Shopping
                                val impCount  = shopping.count { !it.isPlanned }
                                val planCount = shopping.count { it.isPlanned }
                                val impAmt    = shopping
                                    .filter { !it.isPlanned }
                                    .sumOf { it.amount }
                                val impPct = if (shopping.isNotEmpty())
                                    impCount * 100 / shopping.size else 0

                                tvShoppingInsight.text =
                                    "Total items: ${shopping.size}\n" +
                                            "Impulsive: $impCount ($impPct%)\n" +
                                            "Planned: $planCount\n" +
                                            "Impulsive spend: Rs." +
                                            String.format("%.2f", impAmt)

                                // Food
                                val outside = foodList
                                    .filter { it.location == "Outside" }
                                    .sumOf { it.cost }
                                val mess = foodList
                                    .filter { it.location == "Hostel Mess" }
                                    .sumOf { it.cost }
                                val outsidePct = if (foodTotal > 0)
                                    (outside / foodTotal * 100).toInt() else 0

                                tvFoodInsight.text =
                                    "Total food spend: Rs." +
                                            String.format("%.2f", foodTotal) + "\n" +
                                            "Hostel Mess: Rs." +
                                            String.format("%.2f", mess) + "\n" +
                                            "Outside: Rs." +
                                            String.format("%.2f", outside) +
                                            " ($outsidePct%)"

                                // Hardcoded suggestions
                                tvSuggestions.text = buildSuggestions(
                                    pct, outsidePct, impPct
                                )

                                // AI Summary
                                generateAISummary(
                                    budget     = budget,
                                    totalSpent = totalSpent,
                                    pct        = pct,
                                    expCount   = expenses.size,
                                    foodTotal  = foodTotal,
                                    outsidePct = outsidePct,
                                    impPct     = impPct,
                                    topCat     = expenses
                                        .groupBy { it.category }
                                        .maxByOrNull { it.value.sumOf { e -> e.amount } }
                                        ?.key ?: "None"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun generateAISummary(
        budget: Double,
        totalSpent: Double,
        pct: Int,
        expCount: Int,
        foodTotal: Double,
        outsidePct: Int,
        impPct: Int,
        topCat: String
    ) {
        progressAI.visibility = View.VISIBLE
        tvAISummary.text      = "Generating AI summary..."

        val prompt = """
            You are a financial advisor for a college student in India.
            Give a 2-3 sentence personalized summary of their spending this month.
            Be specific, encouraging where possible, and mention one key action.
            Keep it under 60 words.
            
            Data:
            - Monthly budget: Rs.$budget
            - Total spent: Rs.$totalSpent ($pct% of budget)
            - Number of expenses logged: $expCount
            - Food spend: Rs.$foodTotal
            - Outside food: $outsidePct% of food budget
            - Impulsive purchases: $impPct%
            - Top spending category: $topCat
        """.trimIndent()

        lifecycleScope.launch {
            val summary = AIService.getResponse(
                systemPrompt = prompt,
                userMessage  = "Give me my monthly spending summary."
            )
            runOnUiThread {
                progressAI.visibility = View.GONE
                tvAISummary.text      = summary
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
            sb.appendLine("• Used $budgetPct% of budget — reduce discretionary spending.")
        if (outsidePct > 50)
            sb.appendLine("• $outsidePct% food spend is outside — eating mess saves money.")
        if (impulsivePct > 40)
            sb.appendLine("• $impulsivePct% purchases are impulsive — wait 24hrs before buying.")
        if (sb.isEmpty())
            sb.appendLine("• You are on track! Keep logging to get more insights.")
        return sb.toString().trimEnd()
    }
}