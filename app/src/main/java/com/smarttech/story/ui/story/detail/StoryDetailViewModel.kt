package com.smarttech.story.ui.story.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.smarttech.story.database.AppDatabase
import com.smarttech.story.model.*
import com.smarttech.story.networking.DropboxService
import com.smarttech.story.utils.UnzipUtility
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

class StoryDetailViewModel(application: Application, private val storyId: Int, private val storyName: String) : AndroidViewModel(application)  {
    private lateinit var database: DatabaseReference
    private lateinit var db: AppDatabase
    private val _story = MutableLiveData<StoryViewInfo>().apply {
        val storyDao = AppDatabase(application).storyDao()
        value = storyDao.getStoryById(storyId)

    }
    var story: LiveData<StoryViewInfo> = _story



    private val _chapters = MutableLiveData<List<Chapter>>().apply {
        val storyDao = AppDatabase(application).storyDao()


    }
    var chapters: LiveData<List<Chapter>> = _chapters
    /**
     * Navigation for the SleepDetail fragment.
     */
    private val _navigateToCategoryetail = MutableLiveData<Int?>()
    val navigateToSleepDetail
        get() = _navigateToCategoryetail

    fun onCategoryClicked(id: Int) {
        _navigateToCategoryetail.value = id

    }

    fun onCategoryNavigated() {
        _navigateToCategoryetail.value = null
    }
}