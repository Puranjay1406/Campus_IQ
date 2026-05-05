package com.example.campusiq.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campusiq.R
import com.example.campusiq.data.FirestoreHelper
import com.example.campusiq.ui.ProfileActivity
import com.example.campusiq.ui.expense.ExpenseAdapter
import com.example.campusiq.utils.PreferenceManager
import kotlin.math.roundToInt

class ExpenseFragment : Fragment() {

    private lateinit var fs: FirestoreHelper
    private lateinit var prefs: PreferenceManager
    private lateinit var rvExpenses: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var tvTotalSpent: TextView
    private lateinit var tvBudgetLabel: TextView
    private lateinit var tvRemaining: TextView
    private lateinit var progressBudget: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_expense, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fs    = FirestoreHelper()
        prefs = PreferenceManager(requireContext())

        tvEmpty       = view.findViewById(R.id.tvEmpty)
        tvTotalSpent  = view.findViewById(R.id.tvTotalSpent)
        tvBudgetLabel = view.findViewById(R.id.tvBudgetLabel)
        tvRemaining   = view.findViewById(R.id.tvRemaining)
        progressBudget = view.findViewById(R.id.progressBudget)
        rvExpenses    = view.findViewById(R.id.rvExpenses)
        rvExpenses.layoutManager = LinearLayoutManager(requireContext())

        view.findViewById<TextView>(R.id.tvGreeting).text =
            "Hello, ${prefs.studentName.ifEmpty { "Student" }}!"

        view.findViewById<TextView>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }

        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    fun loadData() {
        val budget = prefs.monthlyBudget.toDouble()
        tvBudgetLabel.text = "Rs." + String.format("%.0f", budget)

        fs.getAllExpenses { list ->
            fs.getTotalExpense { total ->
                activity?.runOnUiThread {
                    val progress = if (budget > 0)
                        ((total / budget) * 100).roundToInt().coerceAtMost(100) else 0

                    progressBudget.progress = progress
                    tvTotalSpent.text = "Rs." + String.format("%.0f", total) + " spent"

                    val remaining = budget - total
                    tvRemaining.text = if (remaining >= 0)
                        "Rs." + String.format("%.0f", remaining) + " remaining"
                    else
                        "Over budget by Rs." + String.format("%.0f", -remaining)
                    tvRemaining.setTextColor(
                        if (remaining >= 0)
                            requireContext().getColor(android.R.color.holo_green_dark)
                        else
                            requireContext().getColor(android.R.color.holo_red_dark)
                    )

                    tvEmpty.visibility    = if (list.isEmpty()) View.VISIBLE else View.GONE
                    rvExpenses.visibility = if (list.isEmpty()) View.GONE    else View.VISIBLE
                    rvExpenses.adapter    = ExpenseAdapter(list) { item ->
                        fs.deleteExpense(item.firestoreId) { loadData() }
                    }
                }
            }
        }
    }
}