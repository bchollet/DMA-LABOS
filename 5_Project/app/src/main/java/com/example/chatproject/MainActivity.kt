package com.example.chatproject

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.chatproject.fragment.ui.ChatFragment

class MainActivity : AppCompatActivity() {
    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        chatViewModel.user.observe(this) {
            if (it.isNullOrBlank()) return@observe
            supportFragmentManager.commit {
                replace(R.id.framelayout, ChatFragment.newInstance())
                addToBackStack(null)
            }
        }

        chatViewModel.permissionError.observe(this) {
            Toast.makeText(this, it,  Toast.LENGTH_LONG).show()
        }

    }
}