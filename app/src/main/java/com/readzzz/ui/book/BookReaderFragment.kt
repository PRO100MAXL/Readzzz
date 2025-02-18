package com.readzzz.ui.book

// src/main/java/com/yourpackage/ui/book/BookReaderFragment.kt

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import com.readzzz.databinding.FragmentBookReaderBinding
import com.readzzz.viewmodel.BookViewModel

class BookReaderFragment : Fragment() {

    private var _binding: FragmentBookReaderBinding? = null
    private val binding get() = _binding!!

    private val bookViewModel: BookViewModel by viewModels()

    private var currentTextSize = 16f
    private var isDarkTheme = false

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBookReaderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        loadPreferences()

        val bookId = arguments?.getString("bookId") ?: ""
        val book = bookViewModel.getBookById(bookId)

        if (book != null) {
            binding.textViewContent.text = book.content
            binding.textViewContent.textSize = currentTextSize
            applyTheme()
        } else {
            Toast.makeText(requireContext(), "Book not found", Toast.LENGTH_SHORT).show()
        }

        binding.buttonIncreaseText.setOnClickListener {
            currentTextSize += 2f
            binding.textViewContent.textSize = currentTextSize
            savePreferences()
        }

        binding.buttonDecreaseText.setOnClickListener {
            if (currentTextSize > 8f) {
                currentTextSize -= 2f
                binding.textViewContent.textSize = currentTextSize
                savePreferences()
            }
        }

        binding.buttonToggleTheme.setOnClickListener {
            isDarkTheme = !isDarkTheme
            applyTheme()
            savePreferences()
        }
    }

    private fun applyTheme() {
        if (isDarkTheme) {
            binding.textViewContent.setTextColor(resources.getColor(android.R.color.white))
            binding.scrollViewContent.setBackgroundColor(resources.getColor(android.R.color.black))
        } else {
            binding.textViewContent.setTextColor(resources.getColor(android.R.color.black))
            binding.scrollViewContent.setBackgroundColor(resources.getColor(android.R.color.white))
        }
    }

    private fun loadPreferences() {
        currentTextSize = sharedPreferences.getFloat("textSize", 16f)
        isDarkTheme = sharedPreferences.getBoolean("isDarkTheme", false)
    }

    private fun savePreferences() {
        sharedPreferences.edit {
            putFloat("textSize", currentTextSize)
            putBoolean("isDarkTheme", isDarkTheme)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}