package com.smarttech.story.ui.bookself

import android.media.Image
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.smarttech.story.R
import com.smarttech.story.model.Function

import com.smarttech.story.ui.bookself.dummy.DummyContent.DummyItem

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyBookSelfRecyclerViewAdapter(
    private val values: List<Function>
) : RecyclerView.Adapter<MyBookSelfRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_bookself, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.titleView.text = item.title
        holder.descriptionView.text = item.description
        holder.imageViewFunction.setImageResource(item.thumbnail)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.title)
        val descriptionView: TextView = view.findViewById(R.id.description)
        val imageViewFunction: ImageView = view.findViewById(R.id.imageViewFunction)

        override fun toString(): String {
            return super.toString() + " '" + descriptionView.text + "'"
        }
    }
}