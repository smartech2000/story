package com.smarttech.story.ui.story

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.smarttech.story.database.AppDatabase
import com.smarttech.story.model.Category
import com.smarttech.story.model.CategoryStories
import com.smarttech.story.model.Story
import com.smarttech.story.model.StoryViewInfo
import kotlin.system.measureTimeMillis

class StoryViewModel(application: Application, private val categoryId: Int, private val categoryName: String) : AndroidViewModel(application)  {
    private lateinit var database: DatabaseReference
    private lateinit var db: AppDatabase
    private val _stories = MutableLiveData<List<StoryViewInfo>>().apply {
        val storyDao = AppDatabase(application).storyDao()
        value = storyDao.getStoryByCategoryId(categoryId)
    }
    var stories: LiveData<List<StoryViewInfo>> = _stories
    /**
     * Navigation for the SleepDetail fragment.
     */
    private val _navigateToStoryDetail = MutableLiveData<StoryViewInfo?>()
    val navigateToStoryDetail
        get() = _navigateToStoryDetail

    fun onStoryClicked(storyViewInfo: StoryViewInfo) {
        _navigateToStoryDetail.value = storyViewInfo
    }

    fun onStoryNavigated() {
        _navigateToStoryDetail.value = null
    }
}