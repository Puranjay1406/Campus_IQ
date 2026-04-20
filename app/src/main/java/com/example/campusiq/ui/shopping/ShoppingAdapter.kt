package com.example.campusiq.ui.shopping

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campusiq.R
import com.example.campusiq.data.models.ShoppingItem

class ShoppingAdapter(
    private val items: List<ShoppingItem>,
    private val onDelete: (ShoppingItem) -> Unit
) : RecyclerView.Adapter<ShoppingAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvName:    TextView    = v.findViewById(R.id.tvItemName)
        val tvAmount:  TextView    = v.findViewById(R.id.tvAmount)
        val tvCat:     TextView    = v.findViewById(R.id.tvCategory)
        val tvBadge:   TextView    = v.findViewById(R.id.tvBadge)
        val tvDate:    TextView    = v.findViewById(R.id.tvDate)
        val btnDelete: ImageButton = v.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_shopping, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(h: VH, pos: Int) {
        val item = items[pos]
        h.tvName.text   = item.itemName
        h.tvAmount.text = "Rs." + String.format("%.2f", item.amount)
        h.tvCat.text    = item.category
        h.tvDate.text   = item.date
        if (item.isPlanned) {
            h.tvBadge.text = "Planned"
            h.tvBadge.setTextColor(h.itemView.context.getColor(android.R.color.holo_green_dark))
        } else {
            h.tvBadge.text = "Impulsive"
            h.tvBadge.setTextColor(h.itemView.context.getColor(android.R.color.holo_orange_dark))
        }
        h.btnDelete.setOnClickListener { onDelete(item) }
    }
}