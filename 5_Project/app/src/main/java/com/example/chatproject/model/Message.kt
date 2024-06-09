package com.example.chatproject.model

// model of a message in the DB
data class Message (
    val id: String = "",
    val author: String = "",
    val content: String = ""
)
