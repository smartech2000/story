package com.smarttech.story.ui.story

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smarttech.story.databinding.FragmentStoryBinding
import com.smarttech.story.model.dto.StoryViewInfo
import com.smarttech.story.utils.AvatarUtil
import kotlinx.android.synthetic.main.fragment_story.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*


/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class StoryRecyclerViewAdapter(
    val ctx: Context,
    val clickListener: StoryListener
) : ListAdapter<StoryViewInfo, StoryRecyclerViewAdapter.ViewHolder>(StoryDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var storyViewInfo = getItem(position)
        holder.bind(storyViewInfo!!, clickListener)

        if (storyViewInfo.story.avatar != null && storyViewInfo.story.avatar?.length != 0) {
            // Load avatar
            GlobalScope.launch (Dispatchers.IO) {
                val bmp = AvatarUtil.getBmpFromServer(ctx, storyViewInfo.story.id, storyViewInfo.story.avatar)
                if (bmp != null) {
                    withContext(Dispatchers.Main) {
                        holder.binding.avatar.setImageBitmap(bmp)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    // override fun getItemCount(): Int = values.size

    class ViewHolder private constructor(val binding: FragmentStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StoryViewInfo, clickListener: StoryListener) {
            binding.storyViewInfo = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentStoryBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}

class StoryDiffCallback : DiffUtil.ItemCallback<StoryViewInfo>() {

    override fun areItemsTheSame(oldItem: StoryViewInfo, newItem: StoryViewInfo): Boolean {
        return oldItem.story.id == newItem.story.id
    }

    override fun areContentsTheSame(oldItem: StoryViewInfo, newItem: StoryViewInfo): Boolean {
        return oldItem.story.title == newItem.story.title
    }
}

class StoryListener(val clickListener: (storyViewInfo: StoryViewInfo) -> Unit) {
    fun onClick(storyViewInfo: StoryViewInfo) = clickListener(storyViewInfo)
}
