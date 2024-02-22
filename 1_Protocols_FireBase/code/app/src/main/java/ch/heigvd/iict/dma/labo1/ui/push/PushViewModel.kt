package ch.heigvd.iict.dma.labo1.ui.push

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ch.heigvd.iict.dma.labo1.database.MessagesDao
import ch.heigvd.iict.dma.labo1.repositories.MessagesRepository

class PushViewModel(messageDao : MessagesDao) : ViewModel() {
    private val repository = MessagesRepository(messageDao)
    val lastMessage = repository.lastMessages
}

class PushViewModelFactory(private val messageDao : MessagesDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PushViewModel::class.java)) {
            return PushViewModel(messageDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}