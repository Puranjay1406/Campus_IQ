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

class FoodFragment : Fragment() {

    private lateinit var fs: FirestoreHelper
    private lateinit var prefs: PreferenceManager
    private lateinit var etMenu: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_food, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fs    = FirestoreHelper()
        prefs = PreferenceManager(requireContext())
        etMenu = view.findViewById(R.id.etMenu)

        view.findViewById<Button>(R.id.btnOpenChat).setOnClickListener {
            openFoodChat()
        }
    }

    private fun openFoodChat() {
        val typedMenu = etMenu.text.toString().trim()

        fs.getAllExpenses { expenses ->
            val foodExpenses = expenses.filter { expense ->
                expense.category.equals("Food", ignoreCase = true)
            }
            val totalFood = foodExpenses.sumOf { expense -> expense.amount }
            val budget    = prefs.monthlyBudget

            val contextText = StringBuilder()
            contextText.appendLine("Student: ${prefs.studentName}")
            contextText.appendLine("Monthly budget: Rs.$budget")
            contextText.appendLine("Total food spend this month: Rs.$totalFood")
            contextText.appendLine("Food entries: ${foodExpenses.size}")

            if (typedMenu.isNotEmpty()) {
                contextText.appendLine("Hostel weekly menu:")
                contextText.appendLine(typedMenu.take(600))
            }

            if (foodExpenses.isNotEmpty()) {
                contextText.appendLine("Recent food expenses:")
                for (expense in foodExpenses.take(5)) {
                    val desc = if (expense.description.isNotEmpty())
                        expense.description else expense.category
                    contextText.appendLine(
                        "- $desc: Rs.${expense.amount} on ${expense.date}"
                    )
                }
            }

            activity?.runOnUiThread {
                val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                    putExtra(ChatActivity.EXTRA_CHAT_TYPE, ChatActivity.TYPE_FOOD)
                    putExtra(ChatActivity.EXTRA_CONTEXT_DATA, contextText.toString())
                }
                startActivity(intent)
            }
        }
    }
}