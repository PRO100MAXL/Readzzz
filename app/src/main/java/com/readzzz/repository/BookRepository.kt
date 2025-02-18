package com.readzzz.repository

// src/main/java/com/yourpackage/repository/BookRepository.kt

import com.readzzz.model.Book

class BookRepository {

    fun getBooks(): List<Book> {
        // загрузить книги из базы данных, сети или локального хранилища
        // Для примера возвращаем статический список
        return listOf(
            Book("1", "Title 1", "Author 1", "Content of book 1..."),
            Book("2", "Title 2", "Author 2", "Content of book 2..."),
            // Добавить больше книг
        )
    }

    fun getBookById(id: String): Book? {
        return getBooks().find { it.id == id }
    }
}