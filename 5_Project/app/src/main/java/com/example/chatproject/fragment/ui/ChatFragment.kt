package com.example.chatproject.fragment.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatproject.ChatViewModel
import com.example.chatproject.R
import com.example.chatproject.model.Message
import com.example.chatproject.recyclerview.ChatRecyclerAdapter

class ChatFragment : Fragment() {
    private val chatViewModel: ChatViewModel by activityViewModels()
    private val chatAdapter = ChatRecyclerAdapter(listOf(), this::deleteMessage, this::editMessage)
    private var msgId: String? = null
    private lateinit var messageContent: EditText
    private lateinit var messages: RecyclerView
    private lateinit var button: Button
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       return inflater.inflate(R.layout.fragment_chat, container, false).apply {
           button = findViewById(R.id.sendButton)


           messages = findViewById<RecyclerView?>(R.id.messages).apply {
               adapter = chatAdapter
               layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
           }
           messageContent = findViewById(R.id.messageEditText)

           // setup the "send" button
           findViewById<Button>(R.id.sendButton).setOnClickListener {

               if (msgId != null) {  // if in "edit" mode
                   chatViewModel.editMessage(msgId!!, messageContent.text.toString())
                   button.setText(R.string.send)
                   msgId = null
               } else {
                   chatViewModel.sendMessage(messageContent.text.toString())
               }
               messageContent.text.clear()
           }
       }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatViewModel.messages.observe(viewLifecycleOwner) {
            chatAdapter.items = it
            Log.d("ChatCode", "Received messages - $it")
        }
        chatViewModel.fetchLastMessages()
    }

    private fun deleteMessage(msg: Message) {
        chatViewModel.deleteMessage(msg.id)
    }

    private fun editMessage(msg: Message) {
        msgId = msg.id
        button.setText(R.string.edit)
        messageContent.setText(msg.content)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ChatFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}