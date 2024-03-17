package ch.heigvd.iict.dma.firebase

import android.util.Log
import ch.heigvd.iict.dma.labo1.database.MessagesDatabase
import ch.heigvd.iict.dma.labo1.models.Message
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Calendar

class MessageService : FirebaseMessagingService() {
    private val TAG = "MessageService"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        if(remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            val message = Message(
                sentDate = Calendar.getInstance(),
                receptionDate = Calendar.getInstance(),
                message = remoteMessage.data["message"],
                command = remoteMessage.data["command"]
            )

            MessagesDatabase.getDatabase(applicationContext).messagesDao().insert(message)

            if (remoteMessage.data["message"] == "clear") {
                MessagesDatabase.getDatabase(applicationContext).messagesDao().deleteAllMessage()
            }
        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "TOKEN: ${token}")
    }
}