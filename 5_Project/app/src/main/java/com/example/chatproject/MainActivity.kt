package com.example.chatproject

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.chatproject.fragment.ui.ChatFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Firebase.database.setPersistenceEnabled(false)
        chatViewModel.user.observe(this) {
            if (it.isNullOrBlank()) return@observe
            Log.d("ChatProject", it)
            supportFragmentManager.commit {
                replace(R.id.framelayout, ChatFragment.newInstance())
                addToBackStack(null)
            }
        }
        val fragmentContainerView = findViewById<View>(R.id.framelayout);
        chatViewModel.error.observe(this) {
            Snackbar
                .make(fragmentContainerView, it, Snackbar.LENGTH_SHORT)
                .show()
        }

        chatViewModel.user.observe(this) {
            Snackbar
                .make(fragmentContainerView, getString(R.string.logged_as, it), Snackbar.LENGTH_SHORT)
                .show()
        }

    }
}