package com.example.campusiq.ui.expense

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campusiq.R
import com.example.campusiq.data.DatabaseHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ExpenseActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var rvExpenses: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvTopCat: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)

        db = DatabaseHelper(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        tvEmpty   = findViewById(R.id.tvEmpty)
        tvTotal   = findViewById(R.id.tvTotal)
        tvTopCat  = findViewById(R.id.tvTopCategory)
        rvExpenses = findViewById(R.id.rvExpenses)
        rvExpenses.layoutManager = LinearLayoutManager(this)

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }
    }

    override fun onResume() { super.onResume(); loadData() }

    private fun loadData() {
        val list = db.getAllExpenses()
        tvEmpty.visibility    = if (list.isEmpty()) View.VISIBLE else View.GONE
        rvExpenses.visibility = if (list.isEmpty()) View.GONE    else View.VISIBLE
        rvExpenses.adapter    = ExpenseAdapter(list) { item ->
            db.deleteExpense(item.id); loadData()
        }
        tvTotal.text = "Total: Rs." + String.format("%.2f", db.getTotalExpense())
        val top = db.getExpenseByCategory().maxByOrNull { it.value }
        tvTopCat.text = top?.key ?: "None"
    }
}