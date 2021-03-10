package com.smarttech.story.ui.bookself.history


import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.smarttech.story.R
import com.smarttech.story.databinding.FragmentHistoryBinding
import com.smarttech.story.databinding.FragmentHistoryListBinding
import com.smarttech.story.model.local.HistoryLocal

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class HistoryRecyclerViewAdapter(
    val clickListener: HistoryListener
) : ListAdapter<HistoryLocal, HistoryRecyclerViewAdapter.ViewHolder>(HistoryDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    // override fun getItemCount(): Int = values.size

    class ViewHolder private constructor(val binding: FragmentHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HistoryLocal, clickListener: HistoryListener) {
            binding.historyLocal = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentHistoryBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}

class HistoryDiffCallback : DiffUtil.ItemCallback<HistoryLocal>() {

    override fun areItemsTheSame(oldItem: HistoryLocal, newItem: HistoryLocal): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: HistoryLocal, newItem: HistoryLocal): Boolean {
        return oldItem.name == newItem.name
    }
}

class HistoryListener(val clickListener: (categoryId: Long) -> Unit) {
    fun onClick(history: HistoryLocal) = clickListener(history.id)
}