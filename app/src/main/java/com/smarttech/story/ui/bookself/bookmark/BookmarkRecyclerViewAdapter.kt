package com.smarttech.story.ui.bookself.bookmark


import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.smarttech.story.R
import com.smarttech.story.databinding.FragmentBookmarkBinding
import com.smarttech.story.databinding.FragmentBookmarkListBinding
import com.smarttech.story.model.local.BookmarkLocal

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class BookmarkRecyclerViewAdapter(
    val clickListener: BookmarkListener
) : ListAdapter<BookmarkLocal, BookmarkRecyclerViewAdapter.ViewHolder>(BookmarkDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    // override fun getItemCount(): Int = values.size

    class ViewHolder private constructor(val binding: FragmentBookmarkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BookmarkLocal, clickListener: BookmarkListener) {
            binding.bookmarkLocal = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentBookmarkBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}

class BookmarkDiffCallback : DiffUtil.ItemCallback<BookmarkLocal>() {

    override fun areItemsTheSame(oldItem: BookmarkLocal, newItem: BookmarkLocal): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BookmarkLocal, newItem: BookmarkLocal): Boolean {
        return oldItem.name == newItem.name
    }
}

class BookmarkListener(val clickListener: (categoryId: Long) -> Unit) {
    fun onClick(bookmark: BookmarkLocal) = clickListener(bookmark.id)
}