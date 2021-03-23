package com.smarttech.story.ui.story.detail.desc

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StoryDescViewModelFactory(
    private val application: Application,
    private val storyId: Int
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryDescViewModel::class.java)) {
            return StoryDescViewModel(application,storyId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
