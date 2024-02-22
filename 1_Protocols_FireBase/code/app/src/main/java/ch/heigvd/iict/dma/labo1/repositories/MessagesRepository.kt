package ch.heigvd.iict.dma.labo1.repositories

import ch.heigvd.iict.dma.labo1.database.MessagesDao
import ch.heigvd.iict.dma.labo1.models.Message
import kotlin.concurrent.thread

class MessagesRepository(private val messagesDao: MessagesDao) {

    val lastMessages = messagesDao.getLastMessage()

    fun insert(message : Message) {
        thread {
            messagesDao.insert(message)
        }
    }

    fun deleteAllMessage() {
        thread {
            messagesDao.deleteAllMessage()
        }
    }

}