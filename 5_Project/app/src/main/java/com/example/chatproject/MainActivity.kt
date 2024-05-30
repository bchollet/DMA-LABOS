package com.example.chatproject

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatproject.fragment.ui.ChatFragment
import com.example.chatproject.fragment.ui.LoginFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import androidx.activity.viewModels

class MainActivity : AppCompatActivity() {
    private val chatViewModel: ChatViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setButtonListener()

    }

    private fun setButtonListener() {
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val buttonChat = findViewById<Button>(R.id.buttonChat)

        buttonLogin.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.framelayout, LoginFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        buttonChat.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.framelayout, ChatFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
    }
}