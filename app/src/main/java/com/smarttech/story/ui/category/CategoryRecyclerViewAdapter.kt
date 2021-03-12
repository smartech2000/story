package com.smarttech.story.ui.category

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.smarttech.story.R
import com.smarttech.story.databinding.FragmentCategoryBinding
import com.smarttech.story.databinding.FragmentCategoryListBinding
import com.smarttech.story.model.Category
/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class CategoryRecyclerViewAdapter(
    val clickListener: CategoryListener
) : ListAdapter<Category, CategoryRecyclerViewAdapter.ViewHolder>(CategoryDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    // override fun getItemCount(): Int = values.size

    class ViewHolder private constructor(val binding: FragmentCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Category, clickListener: CategoryListener) {
            binding.category = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentCategoryBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}

class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {

    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.name == newItem.name
    }
}

class CategoryListener(val clickListener: (categoryId: Long) -> Unit) {
    fun onClick(category: Category) = clickListener(category.id)
}