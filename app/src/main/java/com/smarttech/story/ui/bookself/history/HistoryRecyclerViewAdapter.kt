package com.smarttech.story.ui.bookself.history


import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.smarttech.story.databinding.FragmentHistoryBinding
import com.smarttech.story.model.dto.StoryViewInfo
import com.smarttech.story.utils.AvatarUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class HistoryRecyclerViewAdapter(
    val ctx: Context,
    val clickListener: HistoryListener
) : ListAdapter<StoryViewInfo, HistoryRecyclerViewAdapter.ViewHolder>(HistoryDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storyViewInfo = getItem(position)
        holder.bind(storyViewInfo!!, clickListener)
        if (storyViewInfo.story.avatar != null && storyViewInfo.story.avatar?.length != 0) {
            // Load avatar
            GlobalScope.launch (Dispatchers.IO) {
                val bmp = AvatarUtil.getBmpFromServer(ctx, storyViewInfo.story.id, storyViewInfo.story.avatar)
                withContext(Dispatchers.Main) {
                    holder.binding.avatar.setImageBitmap(bmp)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    // override fun getItemCount(): Int = values.size

    class ViewHolder private constructor(val binding: FragmentHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StoryViewInfo, clickListener: HistoryListener) {
            binding.storyViewInfo = item
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


class HistoryDiffCallback : DiffUtil.ItemCallback<StoryViewInfo>() {

    override fun areItemsTheSame(oldItem: StoryViewInfo, newItem: StoryViewInfo): Boolean {
        return oldItem.story.id == newItem.story.id
    }

    override fun areContentsTheSame(oldItem: StoryViewInfo, newItem: StoryViewInfo): Boolean {
        return oldItem.story.title == newItem.story.title
    }
}

class HistoryListener(val clickListener: (storyViewInfo: StoryViewInfo) -> Unit) {
    fun onClick(storyViewInfo: StoryViewInfo) = clickListener(storyViewInfo)
}
