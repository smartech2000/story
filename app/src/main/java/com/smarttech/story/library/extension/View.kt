package com.smarttech.story.library.extension

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.view.ViewTreeObserver
import kotlin.math.max

fun View.getBitmap(_width : Int = 0, _heigth: Int = 0): Bitmap? {
    val mWidth = max(width,_width)
    val mHeight = max(height,_heigth)

    if (mWidth <= 0 || mHeight <= 0) {
        return null
    }
    val returnedBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(returnedBitmap)
    val bgDrawable = background
    if (bgDrawable != null)
        bgDrawable.draw(canvas) else
        canvas.drawColor(Color.WHITE)
    draw(canvas)
    return returnedBitmap
}