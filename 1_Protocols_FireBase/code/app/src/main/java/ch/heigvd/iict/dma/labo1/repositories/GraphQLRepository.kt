package ch.heigvd.iict.dma.labo1.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.heigvd.iict.dma.labo1.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

class GraphQLRepository(private val scope : CoroutineScope, private val httpsUrl : String = "https://mobile.iict.ch/graphql") {

    private val _working = MutableLiveData(false)
    val working : LiveData<Boolean> get() = _working

    private val _authors = MutableLiveData<List<Author>>(emptyList())
    val authors : LiveData<List<Author>> get() = _authors

    private val _books = MutableLiveData<List<Book>>(emptyList())
    val books : LiveData<List<Book>> get() = _books

    private val _requestDuration = MutableLiveData(-1L)
    val requestDuration : LiveData<Long> get() = _requestDuration

    fun resetRequestDuration() {
        _requestDuration.postValue(-1L)
    }

    fun loadAllAuthorsList() {
        scope.launch(Dispatchers.Default) {
            val elapsed = measureTimeMillis {
                // TODO make the request to server
                // fill _authors LiveData with list of all authors

                //placeholder
                _authors.postValue(testAuthors)
            }
            _requestDuration.postValue(elapsed)
        }
    }

    fun loadBooksFromAuthor(author: Author) {
        scope.launch(Dispatchers.Default) {
            val elapsed = measureTimeMillis {
                // TODO make the request to server
                // fill _books LiveData with list of book of the author

                //placeholder
                _books.postValue(testBooks)
            }
            _requestDuration.postValue(elapsed)
        }
    }

    companion object {
        //placeholder data - to remove
        private val testAuthors = listOf(Author(-1, "Test Author", emptyList()))
        private val testBooks = listOf(Book(-1, "Test Title", "01.01.2024", testAuthors))
    }

}