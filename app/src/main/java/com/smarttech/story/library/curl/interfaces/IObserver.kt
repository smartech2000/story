package com.smarttech.story.library.curl.interfaces

interface IObserver {
    fun onDrawFrame()
    fun onPageSizeChanged(width: Int, height: Int)
    fun onSurfaceCreated()
}