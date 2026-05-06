package com.example.campusiq.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.campusiq.R
import com.example.campusiq.ui.expense.AddExpenseActivity
import com.example.campusiq.ui.fragment.ExpenseFragment
import com.example.campusiq.ui.fragment.FoodFragment
import com.example.campusiq.ui.fragment.ShoppingFragment
import com.example.campusiq.ui.insights.InsightsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : BaseActivity() {

    private lateinit var fabAddExpense: FloatingActionButton
    private lateinit var fabInsights: FloatingActionButton
    private lateinit var bottomNav: BottomNavigationView

    private val expenseFragment  = ExpenseFragment()
    private val foodFragment     = FoodFragment()
    private val shoppingFragment = ShoppingFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setStatusBarColor("#1A1A2E")
        setNavBarColor("#1A1A2E")

        window.statusBarColor = android.graphics.Color.parseColor("#1A1A2E")
        window.navigationBarColor = android.graphics.Color.parseColor("#1A1A2E")

        fabAddExpense = findViewById(R.id.fabAddExpense)
        fabInsights   = findViewById(R.id.fabInsights)
        bottomNav     = findViewById(R.id.bottomNav)

        // Load default fragment
        loadFragment(0)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_expenses -> { loadFragment(0); true }
                R.id.nav_food     -> { loadFragment(1); true }
                R.id.nav_shopping -> { loadFragment(2); true }
                else -> false
            }
        }

        fabAddExpense.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        fabInsights.setOnClickListener {
            startActivity(Intent(this, InsightsActivity::class.java))
        }
    }

    private fun loadFragment(index: Int) {
        val fragment = when (index) {
            0 -> expenseFragment
            1 -> foodFragment
            2 -> shoppingFragment
            else -> expenseFragment
        }

        // Show FAB only on expense tab
        fabAddExpense.visibility = if (index == 0) View.VISIBLE else View.GONE

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}