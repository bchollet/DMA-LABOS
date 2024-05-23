package com.example.chatproject.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatproject.R
import com.example.chatproject.model.Message

class ChatRecyclerAdapter(_items: List<Message> = listOf()): RecyclerView.Adapter<ChatRecyclerAdapter.ViewHolder>() {
    var items = listOf<Message>()
        set(value) {
            val diffCallback = ChatDiffCallback(items, value)
            val diffItems = DiffUtil.calculateDiff(diffCallback)
            field = value
            diffItems.dispatchUpdatesTo(this)
        }

    init { items = _items }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val messageAuthor = view.findViewById<TextView>(R.id.list_item_author)
        private val messageContent = view.findViewById<TextView>(R.id.list_item_message)
        fun bind(message: Message) {
            messageAuthor.text = message.author
            messageContent.text = message.content
        }
    }
}