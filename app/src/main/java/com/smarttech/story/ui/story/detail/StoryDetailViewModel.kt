package com.smarttech.story.ui.story.detail

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseReference
import com.smarttech.story.constants.Constants
import com.smarttech.story.constants.Repo
import com.smarttech.story.database.AppDatabase
import com.smarttech.story.database.DescDatabase
import com.smarttech.story.global.ApplicationState
import com.smarttech.story.model.*
import com.smarttech.story.model.dto.ChapterDto
import com.smarttech.story.model.dto.StoryViewInfo
import com.smarttech.story.networking.DropboxService
import com.smarttech.story.utils.FileUtil
import com.smarttech.story.utils.UnzipUtility
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.*
import java.io.File

class StoryDetailViewModel(
    application: Application,
    context: Context,
    private val storyId: Int,
    private val storyName: String
) : AndroidViewModel(application) {
    private lateinit var db: AppDatabase
    private val _story = MutableLiveData<StoryViewInfo>().apply {
        val storyDao = AppDatabase(application).storyDao()
        value = storyDao.getStoryById(storyId)
    }
    var story: LiveData<StoryViewInfo> = _story
}
