package com.example.campusiq.ui.insights

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.campusiq.R
import com.example.campusiq.data.DatabaseHelper
import com.example.campusiq.utils.PreferenceManager

class InsightsActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var prefs: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insights)
        // Makes status bar match the dark header
        window.statusBarColor = android.graphics.Color.parseColor("#1A1A2E")
        // Makes navigation bar match the page background
        window.navigationBarColor = android.graphics.Color.parseColor("#F4F6FB")
        db    = DatabaseHelper(this)
        prefs = PreferenceManager(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        loadInsights()
    }

    private fun loadInsights() {
        val budget     = prefs.monthlyBudget.toDouble()
        val totalSpent = db.getTotalExpense() + db.getTotalFoodCost()
        val pct        = if (budget > 0) (totalSpent / budget * 100).toInt() else 0

        // Budget section
        findViewById<TextView>(R.id.tvTotalSpent).text =
            "Rs." + String.format("%.2f", totalSpent)
        findViewById<ProgressBar>(R.id.progressBudget).progress = pct.coerceAtMost(100)
        findViewById<TextView>(R.id.tvBudgetStatus).text = when {
            pct < 50  -> "Well within budget. Great discipline!"
            pct < 80  -> "Budget usage is getting high. Slow down."
            pct < 100 -> "Almost at the limit! Control spending now."
            else      -> "Over budget! Stop non-essential spending."
        }

        // Top expense category
        val catMap = db.getExpenseByCategory()
        val top    = catMap.maxByOrNull { it.value }
        findViewById<TextView>(R.id.tvTopCategory).text = top?.let {
            it.key + " — Rs." + String.format("%.2f", it.value)
        } ?: "No data yet"

        // Food insights
        val totalFood = db.getTotalFoodCost()
        val locMap    = db.getFoodCostByLocation()
        val outside   = locMap["Outside"] ?: 0.0
        val mess      = locMap["Hostel Mess"] ?: 0.0
        val outsidePct = if (totalFood > 0) (outside / totalFood * 100).toInt() else 0
        findViewById<TextView>(R.id.tvFoodInsight).text =
            "Total food spend: Rs." + String.format("%.2f", totalFood) + "\n" +
                    "Hostel Mess: Rs." + String.format("%.2f", mess) + "\n" +
                    "Outside food: Rs." + String.format("%.2f", outside) + " ($outsidePct%)"

        // Shopping insights
        val (impCount, planCount, impAmt) = db.getShoppingStats()
        val totalShop = impCount + planCount
        val impPct    = if (totalShop > 0) impCount * 100 / totalShop else 0
        findViewById<TextView>(R.id.tvShoppingInsight).text =
            "Total items: $totalShop\n" +
                    "Impulsive: $impCount ($impPct%)\n" +
                    "Planned: $planCount\n" +
                    "Impulsive spend: Rs." + String.format("%.2f", impAmt)

        // Smart suggestions
        findViewById<TextView>(R.id.tvSuggestions).text =
            buildSuggestions(pct, outsidePct, impPct)
    }

    private fun buildSuggestions(budgetPct: Int, outsidePct: Int, impulsivePct: Int): String {
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