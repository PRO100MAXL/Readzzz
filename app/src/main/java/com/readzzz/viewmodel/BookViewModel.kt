package com.readzzz.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import com.readzzz.model.Book
import com.readzzz.repository.BookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookViewModel(private val bookRepository: BookRepository) : ViewModel() {

    private val _books = MutableLiveData<List<Book>>(emptyList())
    val books: LiveData<List<Book>> get() = _books

    /**
     * Добавляем книгу (TXT) из Uri, читая через BookRepository.
     */
    fun addBook(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val newBook = bookRepository.createBookFromUri(uri)
            val updated = _books.value.orEmpty().toMutableList()
            updated.add(newBook)
            _books.postValue(updated)
        }
    }

    /**
     * Удаляем книгу из списка
     */
    fun removeBook(book: Book) {
        val updated = _books.value.orEmpty().toMutableList()
        updated.remove(book)
        _books.value = updated
    }

    fun setBooks(list: List<Book>) {
        _books.value = list
    }

    fun getBookById(id: String): Book? {
        return _books.value?.find { it.id == id }
    }

    // Фабрика
    class Factory(private val bookRepository: BookRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BookViewModel::class.java)) {
                return BookViewModel(bookRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
        }
    }
}