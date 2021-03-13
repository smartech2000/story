package com.smarttech.story.ui.bookself.download

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import com.smarttech.story.database.AppDatabase
import com.smarttech.story.model.local.DownloadLocal

class DownloadViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var db: AppDatabase;
    private val _downloads = MutableLiveData<List<DownloadLocal>>().apply {
        // Access a Cloud Firestore instance from your Activity
        db = AppDatabase(application)
        val downloads =  db.storyDao().getAllDownloadLocal()
        value = downloads
    }
    var downloads: LiveData<List<DownloadLocal>> = _downloads

    /**
     * Navigation for the SleepDetail fragment.
     */
    private val _download = MutableLiveData<DownloadLocal?>()
    val navigateToDownload
        get() = _download

    fun onDownloadClicked(id: Long) {
    }

    fun onDownloadNavigated() {
        _download.value = null
    }
}