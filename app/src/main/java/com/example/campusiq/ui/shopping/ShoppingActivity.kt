package com.example.campusiq.ui.shopping

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

class ShoppingActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var rvShopping: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvImpulsive: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping)
        db = DatabaseHelper(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        tvEmpty     = findViewById(R.id.tvEmpty)
        tvTotal     = findViewById(R.id.tvTotal)
        tvImpulsive = findViewById(R.id.tvImpulsive)
        rvShopping  = findViewById(R.id.rvShopping)
        rvShopping.layoutManager = LinearLayoutManager(this)

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            startActivity(Intent(this, AddShoppingActivity::class.java))
        }
    }

    override fun onResume() { super.onResume(); loadData() }

    private fun loadData() {
        val list = db.getAllShoppingItems()
        tvEmpty.visibility    = if (list.isEmpty()) View.VISIBLE else View.GONE
        rvShopping.visibility = if (list.isEmpty()) View.GONE    else View.VISIBLE
        rvShopping.adapter    = ShoppingAdapter(list) { item ->
            db.deleteShoppingItem(item.id); loadData()
        }
        val total      = list.sumOf { it.amount }
        tvTotal.text   = "Total: Rs." + String.format("%.2f", total)
        val impCount   = list.count { !it.isPlanned }
        tvImpulsive.text = "$impCount impulsive purchases"
    }
}