package com.example.campusiq.ui.chat

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import com.example.campusiq.R
import com.example.campusiq.data.AIService
import com.example.campusiq.ui.BaseActivity
import kotlinx.coroutines.launch

class ChatActivity : BaseActivity() {

    private lateinit var rvMessages: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: ChatAdapter

    private var chatType: String = ""
    private var contextData: String = ""

    // Conversation history for multi-turn chat
    private val conversationHistory = mutableListOf<Pair<String, String>>()

    companion object {
        const val EXTRA_CHAT_TYPE    = "chat_type"
        const val EXTRA_CONTEXT_DATA = "context_data"
        const val TYPE_FOOD          = "food"
        const val TYPE_SHOPPING      = "shopping"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setStatusBarColor("#1A1A2E")
        setNavBarColor("#F4F6FB")

        chatType    = intent.getStringExtra(EXTRA_CHAT_TYPE) ?: ""
        contextData = intent.getStringExtra(EXTRA_CONTEXT_DATA) ?: ""

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = when (chatType) {
            TYPE_FOOD     -> "🍽️ Food Advisor"
            TYPE_SHOPPING -> "🛒 Shopping Advisor"
            else          -> "AI Advisor"
        }
        toolbar.setNavigationOnClickListener { finish() }

        adapter      = ChatAdapter()
        rvMessages   = findViewById(R.id.rvMessages)
        etMessage    = findViewById(R.id.etMessage)
        btnSend      = findViewById(R.id.btnSend)
        progressBar  = findViewById(R.id.progressBar)

        rvMessages.layoutManager = LinearLayoutManager(this).also {
            it.stackFromEnd = true
        }
        rvMessages.adapter = adapter

        // Welcome message
        val welcome = when (chatType) {
            TYPE_FOOD ->
                "Hi! I'm your Food Advisor 🍽️\n\n" +
                        "I have access to your food spending data and menu. " +
                        "Ask me anything about meal choices, saving on food, " +
                        "or what to eat this week!"
            TYPE_SHOPPING ->
                "Hi! I'm your Shopping Advisor 🛒\n\n" +
                        "I can see your budget and spending patterns. " +
                        "Tell me what you want to buy and I'll give you " +
                        "honest advice on whether to go ahead, wait, or " +
                        "find a better alternative!"
            else -> "Hi! How can I help you today?"
        }
        adapter.addMessage(ChatMessage(welcome, isUser = false))

        btnSend.setOnClickListener { sendMessage() }

        // Allow sending with keyboard enter
        etMessage.setOnEditorActionListener { _, _, _ ->
            sendMessage()
            true
        }
    }

    private fun getSystemPrompt(): String = when (chatType) {
        TYPE_FOOD -> """
            You are a helpful food and lifestyle advisor for a hostel student in India.
            You have access to their spending data and menu information below.
            Give practical, specific advice about food choices, meal planning,
            and how to save money on food. Keep responses concise and friendly.
            Always consider their budget when giving advice.
            
            Student context:
            $contextData
        """.trimIndent()

        TYPE_SHOPPING -> """
            You are a smart shopping advisor for a college student in India.
            You have access to their budget and spending history below.
            Give honest advice about whether to buy something, suggest alternatives,
            mention upcoming Indian sale seasons (Big Billion Days, Great Indian Festival,
            Republic Day sales, etc.) when relevant.
            Keep responses concise, practical and friendly.
            Consider their remaining budget seriously.
            
            Student context:
            $contextData
        """.trimIndent()

        else -> "You are a helpful financial advisor for a college student."
    }

    private fun sendMessage() {
        val text = etMessage.text.toString().trim()
        if (text.isEmpty()) return

        // Show user message
        adapter.addMessage(ChatMessage(text, isUser = true))
        etMessage.text.clear()
        scrollToBottom()

        // Show loading
        setLoading(true)

        lifecycleScope.launch {
            val response = AIService.getResponse(
                systemPrompt        = getSystemPrompt(),
                userMessage         = text,
                conversationHistory = conversationHistory
            )

            // Add to history for multi-turn conversation
            conversationHistory.add(Pair("user", text))
            conversationHistory.add(Pair("assistant", response))

            runOnUiThread {
                setLoading(false)
                adapter.addMessage(ChatMessage(response, isUser = false))
                scrollToBottom()
            }
        }
    }

    private fun scrollToBottom() {
        rvMessages.scrollToPosition(adapter.itemCount - 1)
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnSend.isEnabled      = !loading
        btnSend.text           = if (loading) "..." else "Send"
    }
}