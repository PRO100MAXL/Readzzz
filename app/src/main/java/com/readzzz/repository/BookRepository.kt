package com.readzzz.repository

import android.content.Context
import android.net.Uri
import com.readzzz.model.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mozilla.universalchardet.UniversalDetector
import java.io.InputStream
import java.nio.charset.Charset

class BookRepository(private val context: Context) {

    suspend fun createBookFromUri(uri: Uri): Book {
        return withContext(Dispatchers.IO) {
            parseTextFile(uri)
        }
    }

    private fun parseTextFile(uri: Uri): Book {
        // Считываем весь файл в массив байт
        val bytes = openInputStream(uri).use { it.readBytes() }

        // Автоопределяем кодировку
        val detector = UniversalDetector()
        detector.handleData(bytes, 0, bytes.size)
        detector.dataEnd()
        var encoding = detector.detectedCharset
        detector.reset()

        if (encoding.isNullOrBlank()) {
            encoding = "UTF-8"  // если не смогли определить
        }

        // Приведём к верхнему регистру, мало ли "windows-1251" или "WINDOWS-1251"
        encoding = encoding.trim().uppercase()

        // Преобразуем байты в строку, учитывая распознанную кодировку
        // Если это CP1251 -> "WINDOWS-1251"
        // Если UTF-8 -> "UTF-8"
        // Если вдруг вернёт что-то типа SHIFT_JIS, KOI8-R и т.д. – тоже подхватит
        val textContent = bytes.toString(Charset.forName(encoding))

        val fileName = getFileName(uri) ?: "Text File"

        return Book(
            id = uri.toString(),
            title = fileName,
            author = "Unknown",
            path = uri.toString(),
            content = textContent
        )
    }

    private fun openInputStream(uri: Uri): InputStream {
        return context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open stream for $uri")
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndexOrThrow(android.provider.OpenableColumns.DISPLAY_NAME)
                result = it.getString(nameIndex)
            }
        }
        return result
    }
}