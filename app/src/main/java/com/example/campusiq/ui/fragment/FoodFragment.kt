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
        val menu = etMenu.text.toString().trim()

        fs.getAllExpenses { expenses ->
            val foodExpenses = expenses.filter {
                it.category.equals("Food", ignoreCase = true)
            }
            val totalFood  = foodExpenses.sumOf { it.amount }
            val budget     = prefs.monthlyBudget

            val context = buildString {
                appendLine("Student: ${prefs.studentName}")
                appendLine("Monthly budget: Rs.$budget")
                appendLine("Total food spend this month: Rs.$totalFood")
                appendLine("Food entries: ${foodExpenses.size}")
                if (menu.isNotEmpty()) {
                    appendLine("Hostel weekly menu:")
                    appendLine(menu)
                }
                appendLine("Recent food expenses:")
                foodExpenses.take(5).forEach {
                    appendLine("- ${it.category}: Rs.${it.amount} on ${it.date}")
                }
            }

            activity?.runOnUiThread {
                val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                    putExtra(ChatActivity.EXTRA_CHAT_TYPE, ChatActivity.TYPE_FOOD)
                    putExtra(ChatActivity.EXTRA_CONTEXT_DATA, context)
                }
                startActivity(intent)
            }
        }
    }
}