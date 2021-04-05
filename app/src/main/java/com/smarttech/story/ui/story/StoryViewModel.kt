package com.smarttech.story.ui.story

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.smarttech.story.database.AppDatabase
import com.smarttech.story.model.dto.StoryViewInfo

class StoryViewModel(application: Application, private val categoryId: Int, private val categoryName: String) : AndroidViewModel(application)  {
    private lateinit var db: AppDatabase
    private val _stories = MutableLiveData<List<StoryViewInfo>>().apply {
        val storyDao = AppDatabase(application).storyDao()
        if (categoryId != -1) {
            value = storyDao.getStoryByCategoryId(categoryId)
        } else {
            val keySearch = "%"+categoryName+"%"
            value = storyDao.searchStory(keySearch)
        }
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