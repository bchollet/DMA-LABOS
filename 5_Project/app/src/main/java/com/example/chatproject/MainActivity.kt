package com.example.chatproject

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.chatproject.fragment.ui.ChatFragment
import com.example.chatproject.fragment.ui.LoginFragment
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
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