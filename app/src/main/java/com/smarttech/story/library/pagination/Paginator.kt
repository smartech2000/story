package com.smarttech.story.library.pagination

import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.M)
@Suppress("DEPRECATION")
class Paginator(
    text: CharSequence,
    width: Int,
    height: Int,
    paint: TextPaint,
    spacingMult: Float,
    spacingAdd: Float
) {
    private val pages = parsePages(
        text,
        width,
        height,
        paint,
        spacingMult,
        spacingAdd
    )
    var currentIndex = 0
    val pagesCount = pages.size

    fun getCurrentPage(): CharSequence = pages[currentIndex]

    private fun parsePages(
        content: CharSequence,
        width: Int,
        height: Int,
        paint: TextPaint,
        spacingMult: Float,
        spacingAdd: Float
    ): List<CharSequence> {

        val layout =
            when (Build.VERSION.SDK_INT) {
                in Int.MIN_VALUE..Build.VERSION_CODES.M -> StaticLayout(
                    content,
                    paint,
                    width,
                    Layout.Alignment.ALIGN_NORMAL,
                    spacingMult,
                    spacingAdd,
                    true
                )
                else -> StaticLayout.Builder.obtain(content, 0, content.length, paint, width)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(spacingAdd, spacingMult)
                    .setIncludePad(false).build()
            }
        val text = layout.text
        var startOffset = 0
        var lastBottomLineHeight = height


        val parsedPages = mutableListOf<CharSequence>()

        for (i in 0 until layout.lineCount) {
            if (lastBottomLineHeight < layout.getLineBottom(i)) {
                var endLineIndex = layout.getLineStart(i)
                var page = text.subSequence(startOffset, endLineIndex).trim()
                if (page.endsWith("<")) {
                    page = "${page}/p>"
                } else if (page.endsWith("</")) {
                    page = "${page}p>"
                } else if (page.endsWith("</p") || page.endsWith("<br/") || page.endsWith("<br /")) {
                    page = "${page}>"
                } else if (page.endsWith("<br")) {
                    page = "${page} />"
                }
                if (page.startsWith("p>") || page.startsWith("br>") || page.startsWith("br >") || page.startsWith(
                        "br />"
                    )
                ) {
                    page = "<${page}"
                } else if (page.startsWith(">")) {
                    page = "<p${page}"
                } else if (page.startsWith("/p>") || page.startsWith("/>")) {
                    page = page.substring(2)
                }
                parsedPages.add(page)
                startOffset = endLineIndex
                lastBottomLineHeight = layout.getLineTop(i) + height
            }
        }
        val lastPage = text.subSequence(startOffset, layout.getLineEnd(layout.lineCount - 1))
        return parsedPages.plus(lastPage)
    }
}