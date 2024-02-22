package ch.heigvd.iict.dma.labo1.models

data class Book(val id : Int, val title : String, val publicationDate : String, val authors : List<Author>)