package com.example.campusiq.ui.food

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

class FoodActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var rvFood: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvOutside: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)
        db = DatabaseHelper(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        tvEmpty   = findViewById(R.id.tvEmpty)
        tvTotal   = findViewById(R.id.tvTotal)
        tvOutside = findViewById(R.id.tvOutside)
        rvFood    = findViewById(R.id.rvFood)
        rvFood.layoutManager = LinearLayoutManager(this)

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            startActivity(Intent(this, AddFoodActivity::class.java))
        }
    }

    override fun onResume() { super.onResume(); loadData() }

    private fun loadData() {
        val list = db.getAllFoodEntries()
        tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        rvFood.visibility  = if (list.isEmpty()) View.GONE    else View.VISIBLE
        rvFood.adapter     = FoodAdapter(list) { item -> db.deleteFoodEntry(item.id); loadData() }

        val total   = db.getTotalFoodCost()
        tvTotal.text = "Total: Rs." + String.format("%.2f", total)

        val locMap  = db.getFoodCostByLocation()
        val outside = locMap["Outside"] ?: 0.0
        val pct     = if (total > 0) (outside / total * 100).toInt() else 0
        tvOutside.text = "$pct% outside eating"
    }
}