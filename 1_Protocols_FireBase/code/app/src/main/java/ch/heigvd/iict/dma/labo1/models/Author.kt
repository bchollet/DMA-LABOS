package ch.heigvd.iict.dma.labo1.models

data class Author(val id : Int, val name : String, val books : List<Book>?)

data class AuthorResponse(val data: Data)

data class Data(val findAllAuthors: List<Author>, val findAuthorById: Author)