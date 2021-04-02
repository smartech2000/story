package com.smarttech.story.ui.account

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.smarttech.story.R
import com.smarttech.story.databinding.FragmentAccountBinding
import com.smarttech.story.model.Function
import kotlinx.android.synthetic.main.fragment_account.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class BookSelfRecyclerViewAdapter(
    val clickListener: FunctionListener
) : ListAdapter<Function, BookSelfRecyclerViewAdapter.ViewHolder>(FunctionDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
        if (position == 0) {
            holder.itemView.description.setTextColor(Color.RED)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    // override fun getItemCount(): Int = values.size

    class ViewHolder private constructor(val binding: FragmentAccountBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Function, clickListener: FunctionListener) {
            binding.account = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentAccountBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}

class FunctionDiffCallback : DiffUtil.ItemCallback<Function>() {

    override fun areItemsTheSame(oldItem: Function, newItem: Function): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Function, newItem: Function): Boolean {
        return oldItem.title == newItem.title
    }
}

class FunctionListener(val clickListener: (function: Function) -> Unit) {
    fun onClick(function: Function) = clickListener(function)
}