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

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Firebase.database
        db.setPersistenceEnabled(true)
        val salutRef = db.getReference("messages/salut")

        salutRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.value
                // .getValue(T.class) <= essayer
                Log.d("Firebase", "Value is: $value - ref is ${dataSnapshot.ref}")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Firebase", "Failed to read value.", error.toException())
            }
        })

//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        setButtonListener()

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
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