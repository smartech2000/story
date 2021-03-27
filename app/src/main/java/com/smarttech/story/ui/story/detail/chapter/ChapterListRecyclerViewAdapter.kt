package com.smarttech.story.ui.story.detail.chapter


import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.smarttech.story.databinding.FragmentStoryChapterBinding
import com.smarttech.story.model.Chapter
import com.smarttech.story.model.dto.ChapterDto


/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class ChapterListRecyclerViewAdapter(
    val clickListener: ChapterListener
) : ListAdapter<ChapterDto, ChapterListRecyclerViewAdapter.ViewHolder>(StoryDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    // override fun getItemCount(): Int = values.size

    class ViewHolder private constructor(val binding: FragmentStoryChapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChapterDto, clickListener: ChapterListener) {
            binding.chapterDto = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentStoryChapterBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}

class StoryDiffCallback : DiffUtil.ItemCallback<ChapterDto>() {

    override fun areItemsTheSame(oldItem: ChapterDto, newItem: ChapterDto): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChapterDto, newItem: ChapterDto): Boolean {
        return oldItem.title == newItem.title
    }
}

class ChapterListener(val clickListener: (chapterDto: ChapterDto) -> Unit) {
    fun onClick(chapterDto: ChapterDto) = clickListener(chapterDto)
}