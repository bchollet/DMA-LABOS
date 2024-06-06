package com.example.chatproject

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.example.chatproject.fragment.ui.ChatFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class MainActivity : AppCompatActivity() {
    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Firebase.database.setPersistenceEnabled(false)

        val fragmentContainerView = findViewById<View>(R.id.framelayout);
        chatViewModel.user.observe(this) {
            if (it.isNullOrBlank()) return@observe
            Log.d("ChatProject", it)
            supportFragmentManager.commit {
                replace(R.id.framelayout, ChatFragment.newInstance())
                addToBackStack(null)
            }
            // Display a message
            Snackbar
                .make(fragmentContainerView, getString(R.string.logged_as, it), Snackbar.LENGTH_SHORT)
                .show()
        }
        chatViewModel.error.observe(this) {
            Snackbar
                .make(fragmentContainerView, it, Snackbar.LENGTH_SHORT)
                .show()
        }
        askNotificationPermission()

        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications").addOnCompleteListener {
            if (!it.isSuccessful) {
                Log.d("ChatCode", "Unsuccessful subscribtion to topic")
            } else {
                Log.d("ChatCode", "Subscribed to topic")
            }
        }

    }

    private fun startService() {
        Log.d("ChatProject", "Starting service ...")
        val intentService = Intent(this, MessagingService::class.java)
        startService(intentService)
        val channel = NotificationChannel(MessagingService.CHANNEL_ID, MessagingService.CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = getString(R.string.channel_description)
        val notificationManager = getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(channel)
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission(),) { isGranted: Boolean ->
        if (isGranted) {
            startService()
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                startService()
            }
        }
    }
}