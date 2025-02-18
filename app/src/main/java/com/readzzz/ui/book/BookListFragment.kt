package com.readzzz.ui.book

// src/main/java/com/yourpackage/ui/book/BookListFragment.kt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.readzzz.databinding.FragmentBookListBinding
import com.readzzz.viewmodel.BookViewModel
import androidx.recyclerview.widget.RecyclerView

class BookListFragment : Fragment() {

    private var _binding: FragmentBookListBinding? = null
    private val binding get() = _binding!!

    private val bookViewModel: BookViewModel by viewModels()

    private lateinit var bookAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBookListBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bookAdapter = BookAdapter { book ->
            val action = BookListFragmentDirections.actionBookListToBookReader(book.id)
            findNavController().navigate(action)
        }

        binding.recyclerViewBooks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bookAdapter
        }

        bookViewModel.books.observe(viewLifecycleOwner) { books ->
            bookAdapter.submitList(books)
        }

        bookViewModel.loadBooks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}