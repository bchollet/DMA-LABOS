package com.example.chatproject

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MessagingService : FirebaseMessagingService() {
    private val TAG = "ChatMessagingService"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "new token for fcm : $token - registering it")

    }

    // this is only called when the app is in background
    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(TAG, "New message - ${message.notification}")
    }
}