package com.readzzz

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import com.readzzz.repository.BookRepository
import com.readzzz.viewmodel.BookViewModel

class MainActivity : AppCompatActivity() {

    private val bookRepository by lazy { BookRepository(applicationContext) }
    private val bookViewModel: BookViewModel by viewModels { BookViewModel.Factory(bookRepository) }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                Log.d("MainActivity", "Выбран файл: $uri")
                openBook(uri)
            } else {
                Log.d("MainActivity", "Файл не был выбран")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /** Метод вызывается из `BookListFragment` */
    fun pickFile() {
        filePickerLauncher.launch(arrayOf("application/epub+zip", "application/octet-stream", "application/fb2"))
    }

    private fun openBook(uri: Uri) {
        try {
            val fileName = getFileName(uri)
            Log.d("MainActivity", "Добавляем книгу в репозиторий: $fileName")

            bookViewModel.addBook(uri) // Теперь добавляем через ViewModel

        } catch (e: Exception) {
            Log.e("MainActivity", "Ошибка открытия файла: ${e.message}")
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = "unknown_file"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }
}
