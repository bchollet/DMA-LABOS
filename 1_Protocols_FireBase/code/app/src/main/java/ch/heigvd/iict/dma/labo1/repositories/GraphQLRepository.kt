package ch.heigvd.iict.dma.labo1.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.heigvd.iict.dma.labo1.models.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
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
                // fill _authors LiveData with list of all authors
                val gson = Gson()
                val query = "{findAllAuthors{id, name}}"
                val connection = URL(httpsUrl).openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json")
                connection.outputStream.use {
                    it.write(gson.toJson(mapOf("query" to query)).toByteArray())
                }

                val response = connection.inputStream.bufferedReader(Charsets.UTF_8).readText()
                val authors = gson.fromJson(response, AuthorResponse::class.java).data.findAllAuthors

                //placeholder
                _authors.postValue(authors)
            }
            _requestDuration.postValue(elapsed)
        }
    }

    fun loadBooksFromAuthor(author: Author) {
        scope.launch(Dispatchers.Default) {
            val elapsed = measureTimeMillis {
                // TODO make the request to server
                // fill _books LiveData with list of book of the author
                val gson = Gson()
                val query = "{findAuthorById(id: ${author.id}){id, name, books{id, title, publicationDate, authors{id, name}}}}"
                val connection = URL(httpsUrl).openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json")
                connection.outputStream.use {
                    it.write(gson.toJson(mapOf("query" to query)).toByteArray())
                }

                val response = connection.inputStream.bufferedReader(Charsets.UTF_8).readText()
                val books = gson.fromJson(response, AuthorResponse::class.java).data.findAuthorById.books

                //placeholder
                _books.postValue(books.orEmpty())
            }
            _requestDuration.postValue(elapsed)
        }
    }
}