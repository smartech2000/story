package com.smarttech.story.ui.story.detail

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StoryDetailViewModelFactory(
    private val application: Application,
    private val context : Context,
    private val storyId: Int,
    private val storyName: String
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryDetailViewModel::class.java)) {
            return StoryDetailViewModel(application,context,storyId, storyName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
