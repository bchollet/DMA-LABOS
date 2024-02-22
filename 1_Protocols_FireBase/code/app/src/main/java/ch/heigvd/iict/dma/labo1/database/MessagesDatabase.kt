package ch.heigvd.iict.dma.labo1.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ch.heigvd.iict.dma.labo1.database.converters.CalendarConverter
import ch.heigvd.iict.dma.labo1.models.Message

@Database(entities = [Message::class], version = 1, exportSchema = true)
@TypeConverters(CalendarConverter::class)
abstract class MessagesDatabase : RoomDatabase() {

    abstract fun messagesDao() : MessagesDao

    companion object {

        @Volatile
        private var INSTANCE : MessagesDatabase? = null

        fun getDatabase(context: Context) : MessagesDatabase {

            return INSTANCE ?: synchronized(this) {
                val _instance = Room.databaseBuilder(context.applicationContext,
                    MessagesDatabase::class.java, "messages.db")
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = _instance
                _instance
            }
        }
    }

}