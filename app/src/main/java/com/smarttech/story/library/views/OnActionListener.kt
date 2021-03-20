package com.smarttech.story.library.views

import com.smarttech.story.library.pagination.ReadState

interface OnActionListener {

    fun onPageLoaded(state: ReadState)

    fun onClick(paragraph: String)

    fun onLongClick(word: String)

    fun onReady(state: ReadState)
}