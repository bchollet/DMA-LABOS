package com.example.chatproject

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


// https://medium.com/firebase-developers/mobile-app-push-notification-with-firebase-cloud-functions-and-realtime-database-194a82e43ba
class MessagingService : FirebaseMessagingService() {
    companion object {
        val CHANNEL_ID: String = "ChatProject"

    }
    private val TAG = "ChatMessagingService"


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "new token for fcm : $token - registering it")

    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(TAG, "New message - ${message.notification}")

        message.notification?.let {
//            sendLocalNotification(it.title!!, it.body!!)
        }

    }

    private fun sendLocalNotification(title: String, body: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )


        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setAutoCancel(true) //Automatically delete the notification
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setContentText(body)
            .setSound(defaultSoundUri)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(1234, notificationBuilder.build())
    }
}