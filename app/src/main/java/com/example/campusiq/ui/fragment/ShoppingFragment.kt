package com.example.campusiq.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.campusiq.R
import com.example.campusiq.data.FirestoreHelper
import com.example.campusiq.ui.chat.ChatActivity
import com.example.campusiq.utils.PreferenceManager

class ShoppingFragment : Fragment() {

    private lateinit var fs: FirestoreHelper
    private lateinit var prefs: PreferenceManager
    private lateinit var tvSummary: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_shopping, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fs       = FirestoreHelper()
        prefs    = PreferenceManager(requireContext())
        tvSummary = view.findViewById(R.id.tvShoppingSummary)

        loadSummary()

        view.findViewById<Button>(R.id.btnOpenChat).setOnClickListener {
            openShoppingChat()
        }
    }

    private fun loadSummary() {
        fs.getAllExpenses { expenses ->
            val shopping  = expenses.filter {
                it.category.equals("Shopping", ignoreCase = true)
            }
            val total     = shopping.sumOf { it.amount }
            val budget    = prefs.monthlyBudget
            val remaining = budget - expenses.sumOf { it.amount }

            activity?.runOnUiThread {
                tvSummary.text =
                    "Shopping spend: Rs.${String.format("%.0f", total)}\n" +
                            "Budget remaining: Rs.${String.format("%.0f", remaining)}\n" +
                            "Items bought: ${shopping.size}"
            }
        }
    }

    private fun openShoppingChat() {
        fs.getAllExpenses { expenses ->
            val budget    = prefs.monthlyBudget
            val totalSpent = expenses.sumOf { it.amount }
            val remaining = budget - totalSpent

            val context = buildString {
                appendLine("Student: ${prefs.studentName}")
                appendLine("Monthly budget: Rs.$budget")
                appendLine("Total spent this month: Rs.$totalSpent")
                appendLine("Budget remaining: Rs.$remaining")
                appendLine("Recent expenses by category:")
                expenses.groupBy { it.category }
                    .forEach { (cat, list) ->
                        appendLine("- $cat: Rs.${list.sumOf { it.amount }}")
                    }
            }

            activity?.runOnUiThread {
                val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                    putExtra(ChatActivity.EXTRA_CHAT_TYPE, ChatActivity.TYPE_SHOPPING)
                    putExtra(ChatActivity.EXTRA_CONTEXT_DATA, context)
                }
                startActivity(intent)
            }
        }
    }
}