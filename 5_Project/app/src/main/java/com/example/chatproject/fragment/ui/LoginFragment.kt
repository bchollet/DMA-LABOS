package com.example.chatproject.fragment.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.chatproject.ChatViewModel
import com.example.chatproject.R

class LoginFragment : Fragment() {
    private lateinit var nameView: EditText
    private val chatViewModel: ChatViewModel by activityViewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater .inflate(R.layout.fragment_login, container, false) .apply {
            nameView = findViewById(R.id.loginEditText)
            val loginButton = findViewById<Button>(R.id.loginButton)
            loginButton.setOnClickListener {
                tryLoginUser()
            }
        }
    }
    private fun tryLoginUser() {
        if (nameView.text.isBlank()) {
            Toast.makeText(requireContext(), "TU TE MOQUES DE QUI LA", Toast.LENGTH_SHORT).show()
            return
        }
        chatViewModel.login(nameView.text.toString())
    }
}