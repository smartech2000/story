package com.smarttech.story.ui.story.detail


import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.smarttech.story.databinding.FragmentStorydetailBinding
import com.smarttech.story.model.Chapter


/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class StoryDetailRecyclerViewAdapter(
    val clickListener: ChapterListener
) : ListAdapter<Chapter, StoryDetailRecyclerViewAdapter.ViewHolder>(StoryDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    // override fun getItemCount(): Int = values.size

    class ViewHolder private constructor(val binding: FragmentStorydetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Chapter, clickListener: ChapterListener) {
            binding.chapter = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentStorydetailBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}

class StoryDiffCallback : DiffUtil.ItemCallback<Chapter>() {

    override fun areItemsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
        return oldItem.title == newItem.title
    }
}

class ChapterListener(val clickListener: (chapterId: Int) -> Unit) {
    fun onClick(chapter: Chapter) = clickListener(chapter.id)
}