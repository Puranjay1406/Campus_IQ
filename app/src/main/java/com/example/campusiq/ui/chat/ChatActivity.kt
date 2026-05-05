package com.example.campusiq.ui.chat

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campusiq.R
import com.example.campusiq.ui.BaseActivity

class ChatActivity : BaseActivity() {

    private lateinit var rvMessages: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: Button
    private lateinit var adapter: ChatAdapter

    private var chatType: String = ""
    private var contextData: String = ""

    companion object {
        const val EXTRA_CHAT_TYPE    = "chat_type"
        const val EXTRA_CONTEXT_DATA = "context_data"
        const val TYPE_FOOD          = "food"
        const val TYPE_SHOPPING      = "shopping"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        enableImmersiveMode()

        window.statusBarColor = android.graphics.Color.parseColor("#1A1A2E")
        window.navigationBarColor = android.graphics.Color.parseColor("#F4F6FB")

        chatType    = intent.getStringExtra(EXTRA_CHAT_TYPE) ?: ""
        contextData = intent.getStringExtra(EXTRA_CONTEXT_DATA) ?: ""

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = when (chatType) {
            TYPE_FOOD     -> "Food Advisor"
            TYPE_SHOPPING -> "Shopping Advisor"
            else          -> "AI Advisor"
        }
        toolbar.setNavigationOnClickListener { finish() }

        adapter    = ChatAdapter()
        rvMessages = findViewById(R.id.rvMessages)
        etMessage  = findViewById(R.id.etMessage)
        btnSend    = findViewById(R.id.btnSend)

        rvMessages.layoutManager = LinearLayoutManager(this).also {
            it.stackFromEnd = true
        }
        rvMessages.adapter = adapter

        // Welcome message
        val welcome = when (chatType) {
            TYPE_FOOD     -> "Hi! I'm your Food Advisor. Ask me about meal choices, your hostel menu, food habits or how to save on food expenses."
            TYPE_SHOPPING -> "Hi! I'm your Shopping Advisor. Tell me what you want to buy and I'll advise you based on your budget and spending patterns."
            else          -> "Hi! How can I help you today?"
        }
        adapter.addMessage(ChatMessage(welcome, isUser = false))

        btnSend.setOnClickListener { sendMessage() }
    }

    private fun sendMessage() {
        val text = etMessage.text.toString().trim()
        if (text.isEmpty()) return

        adapter.addMessage(ChatMessage(text, isUser = true))
        etMessage.text.clear()
        rvMessages.scrollToPosition(adapter.itemCount - 1)

        // Show typing indicator
        btnSend.isEnabled = false
        btnSend.text = "..."

        // TODO: Call AIService here — coming in next step
        // For now show placeholder
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            adapter.addMessage(ChatMessage(
                "AI integration coming soon! Context loaded: ${chatType}",
                isUser = false
            ))
            rvMessages.scrollToPosition(adapter.itemCount - 1)
            btnSend.isEnabled = true
            btnSend.text = "Send"
        }, 1000)
    }
}