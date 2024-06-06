package com.example.chatproject

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.chatproject.fragment.ui.ChatFragment
import com.google.android.material.snackbar.Snackbar

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
        val fragmentContainerView = findViewById<View>(R.id.framelayout);
        chatViewModel.permissionError.observe(this) {
            Snackbar
                .make(fragmentContainerView, it, Snackbar.LENGTH_SHORT)
                .show()
        }

        chatViewModel.user.observe(this) {
            Snackbar.make(fragmentContainerView, "Connecté en tant que $it", Snackbar.LENGTH_SHORT)
                .show()
        }

    }
}