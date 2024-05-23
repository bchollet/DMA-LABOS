package com.example.chatproject.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.example.chatproject.model.Message

class ChatDiffCallback(private val oldList: List<Message>, private val newList: List<Message>): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]
        return old::class == new::class && old.content == new.content
    }

}