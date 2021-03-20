package com.smarttech.story.library.pagination

import android.text.TextPaint

class PaginationController(
    text: CharSequence,
    width: Int,
    height: Int,
    paint: TextPaint,
    spacingMult: Float,
    spacingExtra: Float
) {
    private val paginator = Paginator(text, width, height, paint, spacingMult, spacingExtra)

    fun getCurrentPage() = ReadState(
        paginator.currentIndex + 1,
        paginator.pagesCount,
        getReadPercent(),
        paginator.getCurrentPage()
    )

    fun getNextPage(): ReadState? = paginator
        .takeIf { it.currentIndex < it.pagesCount - 1 }
        ?.also { it.currentIndex++ }
        ?.let { getCurrentPage() }

    fun getPrevPage(): ReadState? = paginator
        .takeIf { it.currentIndex > 0 }
        ?.also { it.currentIndex-- }
        ?.let { getCurrentPage() }

    private fun getReadPercent(): Float = when (paginator.pagesCount) {
        0 -> 0f
        else -> (paginator.currentIndex + 1) / paginator.pagesCount.toFloat() * 100
    }

    fun getPage(index: Int) = paginator
        .takeIf { index <= it.pagesCount - 1 && index >= 0 }
        ?.also { it.currentIndex = index }
        ?.let { getCurrentPage() }
}