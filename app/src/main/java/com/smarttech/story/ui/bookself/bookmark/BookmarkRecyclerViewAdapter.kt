package com.smarttech.story.ui.bookself.bookmark


import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.smarttech.story.databinding.FragmentBookmarkBinding
import com.smarttech.story.model.dto.StoryViewInfo

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class BookmarkRecyclerViewAdapter(
    val clickListener: BookmarkListener
) : ListAdapter<StoryViewInfo, BookmarkRecyclerViewAdapter.ViewHolder>(BookmarkDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    // override fun getItemCount(): Int = values.size

    class ViewHolder private constructor(val binding: FragmentBookmarkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StoryViewInfo, clickListener: BookmarkListener) {
            binding.storyViewInfo = item
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
class BookmarkDiffCallback : DiffUtil.ItemCallback<StoryViewInfo>() {

    override fun areItemsTheSame(oldItem: StoryViewInfo, newItem: StoryViewInfo): Boolean {
        return oldItem.story.id == newItem.story.id
    }

    override fun areContentsTheSame(oldItem: StoryViewInfo, newItem: StoryViewInfo): Boolean {
        return oldItem.story.title == newItem.story.title
    }
}

class BookmarkListener(val clickListener: (storyViewInfo: StoryViewInfo) -> Unit) {
    fun onClick(storyViewInfo: StoryViewInfo) = clickListener(storyViewInfo)
}

