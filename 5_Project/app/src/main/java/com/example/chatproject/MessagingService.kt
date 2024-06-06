package com.example.chatproject

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("ChatProject", "new token pour fcm : $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        message.notification?.let {
            Log.d("ChatProject", "New notification - $it")
        }
    }

}