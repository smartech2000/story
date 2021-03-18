package com.smarttech.story.ui.story.chapter

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChapterViewModelFactory(
    private val application: Application,
    private val chapterKey: String,
    private val chapterIndex: Int
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChapterViewModel::class.java)) {
            return ChapterViewModel(application,chapterKey,chapterIndex) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
