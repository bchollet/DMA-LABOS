package ch.heigvd.iict.dma.labo1.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ch.heigvd.iict.dma.labo1.models.Message

@Dao
interface MessagesDao {

    @Insert
    fun insert(message: Message) : Long

    @Query("SELECT * FROM Message ORDER BY sentDate DESC LIMIT 1")
    fun getLastMessage() : LiveData<Message>

    @Query("DELETE FROM Message")
    fun deleteAllMessage()

}