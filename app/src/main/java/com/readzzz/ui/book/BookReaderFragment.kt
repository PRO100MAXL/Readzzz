package com.readzzz.ui.book

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.readzzz.MyApp
import com.readzzz.databinding.FragmentBookReaderBinding
import com.readzzz.viewmodel.BookViewModel

class BookReaderFragment : Fragment() {

    private var _binding: FragmentBookReaderBinding? = null
    private val binding get() = _binding!!

    private val bookViewModel: BookViewModel by activityViewModels {
        BookViewModel.Factory((requireActivity().application as MyApp).bookRepository)
    }

    private lateinit var prefs: SharedPreferences
    private var bookId: String = ""

    private var currentTextSize = 16f
    private var isDarkTheme = false
    private var scrollYPosition = 0

    // Храним слушатель, чтобы потом удалить
    private lateinit var scrollListener: ViewTreeObserver.OnScrollChangedListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookReaderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // "Назад" &rarr; вернуться к списку книг
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        // Инициализируем SharedPreferences
        prefs = requireContext().getSharedPreferences("reader_prefs", Context.MODE_PRIVATE)

        // Получаем bookId и текст книги
        bookId = arguments?.getString("bookId") ?: ""
        val book = bookViewModel.getBookById(bookId)
        if (book != null) {
            binding.textViewContent.text = book.content
        } else {
            Toast.makeText(requireContext(), "Книга не найдена", Toast.LENGTH_SHORT).show()
        }

        // Грузим старые настройки (тему, шрифт, прокрутку)
        loadReaderSettings()
        applyTheme()
        binding.textViewContent.textSize = currentTextSize

        // Прокрутка к сохранённой позиции
        binding.scrollViewContent.post {
            binding.scrollViewContent.scrollTo(0, scrollYPosition)
        }

        // Кнопки A+, A-, Theme
        binding.buttonIncreaseText.setOnClickListener {
            currentTextSize += 2f
            binding.textViewContent.textSize = currentTextSize
        }

        binding.buttonDecreaseText.setOnClickListener {
            if (currentTextSize > 8f) {
                currentTextSize -= 2f
                binding.textViewContent.textSize = currentTextSize
            }
        }

        binding.buttonToggleTheme.setOnClickListener {
            isDarkTheme = !isDarkTheme
            applyTheme()
        }

        // Создаём listener только сейчас, когда binding не null
        scrollListener = ViewTreeObserver.OnScrollChangedListener {
            // Защищаемся: если binding уже null (событие в очереди), прерываемся
            val safeBinding = _binding ?: return@OnScrollChangedListener
            scrollYPosition = safeBinding.scrollViewContent.scrollY
        }

        // Добавляем
        binding.scrollViewContent.viewTreeObserver.addOnScrollChangedListener(scrollListener)
    }

    override fun onPause() {
        super.onPause()
        saveReaderSettings()
    }

    override fun onDestroyView() {
        // Снимаем listener
        binding.scrollViewContent.viewTreeObserver.removeOnScrollChangedListener(scrollListener)

        // Потом обнуляем binding
        _binding = null
        super.onDestroyView()
    }

    private fun loadReaderSettings() {
        currentTextSize = prefs.getFloat("textSize_$bookId", 16f)
        isDarkTheme = prefs.getBoolean("darkTheme_$bookId", false)
        scrollYPosition = prefs.getInt("scrollPos_$bookId", 0)
    }

    private fun saveReaderSettings() {
        prefs.edit()
            .putFloat("textSize_$bookId", currentTextSize)
            .putBoolean("darkTheme_$bookId", isDarkTheme)
            .putInt("scrollPos_$bookId", scrollYPosition)
            .apply()
    }

    private fun applyTheme() {
        if (isDarkTheme) {
            binding.textViewContent.setTextColor(Color.WHITE)
            binding.scrollViewContent.setBackgroundColor(Color.BLACK)
        } else {
            binding.textViewContent.setTextColor(Color.BLACK)
            binding.scrollViewContent.setBackgroundColor(Color.WHITE)
        }
    }
}