package com.example.chatproject.fragment.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatproject.ChatViewModel
import com.example.chatproject.R
import com.example.chatproject.model.Message
import com.example.chatproject.recyclerview.ChatRecyclerAdapter
import com.google.firebase.database.core.RepoManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatFragment : Fragment() {
    private val chatViewModel: ChatViewModel by activityViewModels()
    private val chatAdapter = ChatRecyclerAdapter()
    private lateinit var messageContent: EditText
    private lateinit var messages: RecyclerView;
    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Linkage de la Recycler View et de son Adapter
        val viewRes = inflater.inflate(R.layout.fragment_chat, container, false)
        //Firebase.database.setPersistenceEnabled(true) // must not be in ChatViewModel or it crashes
        messages = viewRes.findViewById<RecyclerView?>(R.id.messages).apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        messageContent = viewRes.findViewById(R.id.messageEditText)

        viewRes.findViewById<Button>(R.id.sendButton).apply {
            setOnClickListener {
                chatViewModel.sendMessage("auteur", messageContent.text.toString())
                messageContent.text.clear()
            }
        }

        return viewRes
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatViewModel.messages.observe(viewLifecycleOwner) {
            Log.d("ChatCode", it.toString())
            chatAdapter.items = it
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ChatFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}