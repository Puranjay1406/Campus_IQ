package com.example.campusiq.ui.expense

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import com.example.campusiq.R
import com.example.campusiq.data.FirestoreHelper
import com.example.campusiq.ui.BaseActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ExpenseActivity : BaseActivity() {

    private lateinit var fs: FirestoreHelper
    private lateinit var rvExpenses: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvTopCat: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)
        setStatusBarColor("#1A1A2E")
        setNavBarColor("#F4F6FB")
        applySystemBarInsets(R.id.rootExpense)

        fs = FirestoreHelper()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        tvEmpty    = findViewById(R.id.tvEmpty)
        tvTotal    = findViewById(R.id.tvTotal)
        tvTopCat   = findViewById(R.id.tvTopCategory)
        rvExpenses = findViewById(R.id.rvExpenses)
        rvExpenses.layoutManager = LinearLayoutManager(this)

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        fs.getAllExpenses { list ->
            runOnUiThread {
                tvEmpty.visibility    = if (list.isEmpty()) View.VISIBLE else View.GONE
                rvExpenses.visibility = if (list.isEmpty()) View.GONE    else View.VISIBLE
                rvExpenses.adapter    = ExpenseAdapter(list) { item ->
                    fs.deleteExpense(item.firestoreId) { loadData() }
                }
            }
        }
        fs.getTotalExpense { total ->
            runOnUiThread {
                tvTotal.text = "Rs." + String.format("%.2f", total)
            }
        }
        fs.getExpenseByCategory { map ->
            runOnUiThread {
                val top = map.maxByOrNull { it.value }
                tvTopCat.text = top?.key ?: "None"
            }
        }
    }
}