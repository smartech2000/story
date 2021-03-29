package com.smarttech.story.ui.bookself.download

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.smarttech.story.database.AppDatabase
import com.smarttech.story.model.dto.StoryViewInfo

class DownloadViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var db: AppDatabase;
    private val _downloads = MutableLiveData<List<StoryViewInfo>>().apply {
        db = AppDatabase(application)
        val downloads =  db.storyDao().findAllDownload()
        value = downloads
    }
    var downloads: LiveData<List<StoryViewInfo>> = _downloads

    /**
     * Navigation for the SleepDetail fragment.
     */
    private val _download = MutableLiveData<StoryViewInfo?>()
    val navigateToDownload
        get() = _download

    fun onDownloadClicked(storyViewInfo: StoryViewInfo) {
    }

    fun onDownloadNavigated() {
        _download.value = null
    }
}