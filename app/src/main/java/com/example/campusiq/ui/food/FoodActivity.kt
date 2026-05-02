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
import com.example.campusiq.data.FirestoreHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FoodActivity : AppCompatActivity() {

    private lateinit var fs: FirestoreHelper
    private lateinit var rvFood: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvOutside: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)
        window.statusBarColor = android.graphics.Color.parseColor("#1A1A2E")
        window.navigationBarColor = android.graphics.Color.parseColor("#F4F6FB")

        fs = FirestoreHelper()

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
        fs.getAllFoodEntries { list ->
            runOnUiThread {
                tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                rvFood.visibility  = if (list.isEmpty()) View.GONE    else View.VISIBLE
                rvFood.adapter     = FoodAdapter(list) { item ->
                    fs.deleteFoodEntry(item.firestoreId) { loadData() }
                }
                val total    = list.sumOf { it.cost }
                tvTotal.text = "Rs." + String.format("%.2f", total)
                val outside  = list.filter { it.location == "Outside" }.sumOf { it.cost }
                val pct      = if (total > 0) (outside / total * 100).toInt() else 0
                tvOutside.text = "$pct% outside"
            }
        }
    }
}