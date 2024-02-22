package ch.heigvd.iict.dma.labo1

import android.app.Application
import ch.heigvd.iict.dma.labo1.database.MessagesDatabase

class Labo1Application : Application() {

    val messagesDao by lazy {
        MessagesDatabase.getDatabase(this).messagesDao()
    }

}