package com.smarttech.story.library.extension

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View

fun View.getBitmap(): Bitmap{
    val returnedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(returnedBitmap)
    val bgDrawable = background
    if (bgDrawable != null)
        bgDrawable.draw(canvas) else
        canvas.drawColor(Color.WHITE)
    draw(canvas)
    return returnedBitmap
}