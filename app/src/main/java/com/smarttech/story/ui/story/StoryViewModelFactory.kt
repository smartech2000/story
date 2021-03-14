package com.smarttech.story.ui.story

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StoryViewModelFactory(
    private val application: Application,
    private val categoryId: Int,
    private val categoryName: String
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(application,categoryId, categoryName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
