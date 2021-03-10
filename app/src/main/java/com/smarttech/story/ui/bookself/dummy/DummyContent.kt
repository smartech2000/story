package com.smarttech.story.ui.bookself.dummy

import com.smarttech.story.R
import com.smarttech.story.model.Function
import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<Function> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    val ITEM_MAP: MutableMap<Int, Function> = HashMap()

    private val COUNT = 25

    init {
        // Add some sample items.
/*        for (i in 1..COUNT) {
            addItem(createDummyItem(i))
        }*/

        var function = Function(1, "Lịch sử", "Lịch sử đọc truyện", R.drawable.ic_baseline_history_24)
        addItem(function)
        function = Function(2, "Tải về", "Truyện đã được tải về", R.drawable.ic_baseline_cloud_download_24)
        addItem(function)
        function = Function(3, "Đánh dấu", "Truyện đã được đánh dấu", R.drawable.ic_baseline_bookmark_24)
        addItem(function)
    }

    private fun addItem(item: Function) {
        ITEMS.add(item)
        ITEM_MAP.put(item.id, item)
    }

    private fun createDummyItem(position: Int): DummyItem {
        return DummyItem(position.toString(), "Item " + position, makeDetails(position))
    }

    private fun makeDetails(position: Int): String {
        val builder = StringBuilder()
        builder.append("Details about Item: ").append(position)
        for (i in 0..position - 1) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }

    /**
     * A dummy item representing a piece of content.
     */
    data class DummyItem(val id: String, val content: String, val details: String) {
        override fun toString(): String = content
    }
}