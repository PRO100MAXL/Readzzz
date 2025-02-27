package com.readzzz.ui.book

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.readzzz.databinding.ItemBookBinding
import com.readzzz.model.Book

/**
 * Адаптер принимает два лямбда-колбэка:
 *  onBookClick – нажимаем на карточку (TextView) и открываем книгу
 *  onRemoveClick – нажимаем "Удалить"
 */
class BookAdapter(
    private val onBookClick: (Book) -> Unit,
    private val onRemoveClick: (Book) -> Unit
) : ListAdapter<Book, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    class BookViewHolder(
        private val binding: ItemBookBinding,
        private val onBookClick: (Book) -> Unit,
        private val onRemoveClick: (Book) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            // Заполняем поля
            binding.textViewTitle.text = book.title
            binding.textViewAuthor.text = book.author

            // При клике на весь элемент (root) – вызвать onBookClick
            binding.root.setOnClickListener {
                onBookClick(book)
            }

            // При нажатии на кнопку "Удалить" – вызвать onRemoveClick
            binding.buttonRemove.setOnClickListener {
                onRemoveClick(book)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookViewHolder(binding, onBookClick, onRemoveClick)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
    override fun areItemsTheSame(oldItem: Book, newItem: Book) = (oldItem.id == newItem.id)
    override fun areContentsTheSame(oldItem: Book, newItem: Book) = (oldItem == newItem)
}