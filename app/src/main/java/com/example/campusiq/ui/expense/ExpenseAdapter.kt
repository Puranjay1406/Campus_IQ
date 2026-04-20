package com.example.campusiq.ui.expense

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campusiq.R
import com.example.campusiq.data.models.Expense

class ExpenseAdapter(
    private val items: List<Expense>,
    private val onDelete: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvCategory:    TextView    = v.findViewById(R.id.tvCategory)
        val tvAmount:      TextView    = v.findViewById(R.id.tvAmount)
        val tvDescription: TextView    = v.findViewById(R.id.tvDescription)
        val tvDate:        TextView    = v.findViewById(R.id.tvDate)
        val tvImpulsive:   TextView    = v.findViewById(R.id.tvImpulsive)
        val btnDelete:     ImageButton = v.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(h: VH, pos: Int) {
        val item = items[pos]
        h.tvCategory.text    = item.category
        h.tvAmount.text      = "Rs." + String.format("%.2f", item.amount)
        h.tvDescription.text = item.description.ifEmpty { "No description" }
        h.tvDate.text        = item.date
        h.tvImpulsive.visibility = if (item.isImpulsive) View.VISIBLE else View.GONE
        h.btnDelete.setOnClickListener { onDelete(item) }
    }
}