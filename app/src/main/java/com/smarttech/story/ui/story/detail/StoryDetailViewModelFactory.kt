package com.smarttech.story.ui.story.detail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StoryDetailViewModelFactory(
    private val application: Application,
    private val storyId: Int,
    private val storyName: String
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryDetailViewModel::class.java)) {
            return StoryDetailViewModel(application,storyId, storyName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
