package com.smarttech.story.library.pagination

import android.graphics.Bitmap

data class ReadState(
    val currentIndex: Int,
    val pagesCount: Int,
    val readPercent: Float,
    val pageText: CharSequence
) {
    var page : Any? = null
}