package com.smarttech.story.ui.bookself.download


import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.smarttech.story.R
import com.smarttech.story.databinding.FragmentDownloadBinding
import com.smarttech.story.databinding.FragmentDownloadListBinding
import com.smarttech.story.model.local.DownloadLocal

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class DownloadRecyclerViewAdapter(
    val clickListener: DownloadListener
) : ListAdapter<DownloadLocal, DownloadRecyclerViewAdapter.ViewHolder>(DownloadDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    // override fun getItemCount(): Int = values.size

    class ViewHolder private constructor(val binding: FragmentDownloadBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DownloadLocal, clickListener: DownloadListener) {
            binding.downloadLocal = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentDownloadBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}

class DownloadDiffCallback : DiffUtil.ItemCallback<DownloadLocal>() {

    override fun areItemsTheSame(oldItem: DownloadLocal, newItem: DownloadLocal): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DownloadLocal, newItem: DownloadLocal): Boolean {
        return oldItem.name == newItem.name
    }
}

class DownloadListener(val clickListener: (categoryId: Long) -> Unit) {
    fun onClick(download: DownloadLocal) = clickListener(download.id)
}