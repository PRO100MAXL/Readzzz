package com.readzzz

import android.app.Application
import com.readzzz.repository.BookRepository

class MyApp : Application() {

    // Инициализируем BookRepository с контекстом приложения
    val bookRepository by lazy {
        BookRepository(applicationContext)  // Используем applicationContext вместо 'this'
    }

    override fun onCreate() {
        super.onCreate()
        // Если нужно — дополнительная инициализация
    }
}