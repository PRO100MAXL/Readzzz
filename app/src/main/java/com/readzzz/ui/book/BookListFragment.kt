package com.readzzz.ui.book

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.readzzz.MyApp
import com.readzzz.R
import com.readzzz.databinding.FragmentBookListBinding
import com.readzzz.model.Book
import com.readzzz.viewmodel.BookViewModel

class BookListFragment : Fragment() {

    private var _binding: FragmentBookListBinding? = null
    private val binding get() = _binding!!

    private val bookViewModel: BookViewModel by activityViewModels {
        BookViewModel.Factory((requireActivity().application as MyApp).bookRepository)
    }

    private lateinit var bookAdapter: BookAdapter

    private val gson = Gson()
    private val prefs by lazy {
        requireContext().getSharedPreferences("books_prefs", Context.MODE_PRIVATE)
    }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedUri: Uri? = result.data?.data
                selectedUri?.let {
                    bookViewModel.addBook(it)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Переопределяем системную кнопку "Назад":
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // Вернуться к экрану авторизации:
            findNavController().navigate(R.id.action_bookListFragment_to_loginFragment)
        }

        // Адаптер с двумя коллбэками
        bookAdapter = BookAdapter(
            onBookClick = { book ->
                val action = BookListFragmentDirections.actionBookListToBookReader(book.id)
                findNavController().navigate(action)
            },
            onRemoveClick = { book ->
                // Удалить книгу
                bookViewModel.removeBook(book)
            }
        )

        binding.recyclerViewBooks.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewBooks.adapter = bookAdapter

        // Восстанавливаем список из SharedPreferences
        val savedBooks = loadBooksFromPrefs()
        if (savedBooks.isNotEmpty()) {
            // Заполняем ViewModel
            bookViewModel.setBooks(savedBooks)
        }

        // Подписываемся на LiveData
        bookViewModel.books.observe(viewLifecycleOwner) { books ->
            binding.progressBar.visibility = View.GONE
            if (books.isEmpty()) {
                binding.textViewEmpty.visibility = View.VISIBLE
            } else {
                binding.textViewEmpty.visibility = View.GONE
            }
            bookAdapter.submitList(books)

            // Сохраняем обновлённый список
            saveBooksToPrefs(books)
        }

        binding.btnPickFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/plain"
            }
            filePickerLauncher.launch(intent)
        }
    }

    private fun saveBooksToPrefs(books: List<Book>) {
        val json = gson.toJson(books)
        prefs.edit().putString("books_list", json).apply()
    }

    private fun loadBooksFromPrefs(): List<Book> {
        val json = prefs.getString("books_list", null) ?: return emptyList()
        val type = object : TypeToken<List<Book>>() {}.type
        return gson.fromJson(json, type)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}