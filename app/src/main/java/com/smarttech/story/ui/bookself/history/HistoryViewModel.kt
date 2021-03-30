package com.smarttech.story.ui.bookself.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.smarttech.story.database.AppDatabase
import com.smarttech.story.model.dto.ChapterDto
import com.smarttech.story.model.dto.StoryViewInfo
import com.smarttech.story.model.local.History

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var db: AppDatabase;
    private val _histories = MutableLiveData<List<StoryViewInfo>>().apply {
        // Access a Cloud Firestore instance from your Activity
        db = AppDatabase(application)
        val histories =  db.storyDao().findAllHistory()
        value = histories
    }
    var histories: LiveData<List<StoryViewInfo>> = _histories

    /**
     * Navigation for the SleepDetail fragment.
     */


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