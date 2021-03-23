package com.smarttech.story.ui.story.detail.chapter

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChapterListViewModelFactory(
    private val application: Application,
    private val context : Context,
    private val storyId: Int
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChapterListViewModel::class.java)) {
            return ChapterListViewModel(application,context,storyId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
