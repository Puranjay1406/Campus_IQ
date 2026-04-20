package com.example.campusiq.ui.food

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campusiq.R
import com.example.campusiq.data.models.FoodEntry

class FoodAdapter(
    private val items: List<FoodEntry>,
    private val onDelete: (FoodEntry) -> Unit
) : RecyclerView.Adapter<FoodAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvItem:     TextView    = v.findViewById(R.id.tvFoodItem)
        val tvMeal:     TextView    = v.findViewById(R.id.tvMealType)
        val tvCost:     TextView    = v.findViewById(R.id.tvCost)
        val tvLocation: TextView    = v.findViewById(R.id.tvLocation)
        val tvMood:     TextView    = v.findViewById(R.id.tvMood)
        val tvDate:     TextView    = v.findViewById(R.id.tvDate)
        val btnDelete:  ImageButton = v.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(h: VH, pos: Int) {
        val item = items[pos]
        h.tvItem.text     = item.foodItem
        h.tvMeal.text     = item.mealType
        h.tvCost.text     = "Rs." + String.format("%.2f", item.cost)
        h.tvLocation.text = item.location
        h.tvMood.text     = "Mood: " + item.mood
        h.tvDate.text     = item.date
        h.btnDelete.setOnClickListener { onDelete(item) }
    }
}