package com.readzzz.viewmodel

// src/main/java/com/yourpackage/viewmodel/BookViewModel.kt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.readzzz.model.Book
import com.readzzz.repository.BookRepository

class BookViewModel : ViewModel() {

    private val bookRepository = BookRepository()

    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> get() = _books

    fun loadBooks() {
        // Загрузка списка книг из репозитория
        _books.value = bookRepository.getBooks()
    }
    // В BookViewModel.kt
    fun getBookById(id: String): Book? {
        return bookRepository.getBookById(id)
    }
}