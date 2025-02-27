package com.readzzz.model

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val path: String,
    val content: String = "" // Добавлено поле content
)
