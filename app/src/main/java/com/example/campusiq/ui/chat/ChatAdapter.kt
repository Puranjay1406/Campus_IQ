package com.example.campusiq.ui.chat

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campusiq.R

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

class ChatAdapter(
    private val messages: MutableList<ChatMessage> = mutableListOf()
) : RecyclerView.Adapter<ChatAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val container: LinearLayout = v.findViewById(R.id.messageContainer)
        val tvMessage: TextView     = v.findViewById(R.id.tvMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false))

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(h: VH, pos: Int) {
        val msg = messages[pos]
        h.tvMessage.text = msg.text

        if (msg.isUser) {
            h.container.gravity = Gravity.END
            h.tvMessage.setBackgroundResource(R.drawable.bg_bubble_user)
            h.tvMessage.setTextColor(Color.WHITE)
        } else {
            h.container.gravity = Gravity.START
            h.tvMessage.setBackgroundResource(R.drawable.bg_bubble_ai)
            h.tvMessage.setTextColor(Color.parseColor("#1A1A2E"))
        }
    }

    fun addMessage(msg: ChatMessage) {
        messages.add(msg)
        notifyItemInserted(messages.size - 1)
    }
}