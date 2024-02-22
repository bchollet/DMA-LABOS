package ch.heigvd.iict.dma.labo1.ui.graphql

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.heigvd.iict.dma.labo1.repositories.GraphQLRepository
import ch.heigvd.iict.dma.labo1.models.Author

class GraphQlViewModel : ViewModel() {

    private val repository = GraphQLRepository(viewModelScope)

    val loading = repository.working
    val authors = repository.authors
    val books = repository.books
    val requestDuration = repository.requestDuration

    fun loadBooksFromAuthor(author: Author) {
        repository.loadBooksFromAuthor(author)
    }

    fun resetRequestDuration() {
        repository.resetRequestDuration()
    }

    init {
        // we load authors' list immediately
        repository.loadAllAuthorsList()
    }

}