package com.smarttech.story.ui.bookself.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import com.smarttech.story.database.AppDatabase
import com.smarttech.story.model.local.HistoryLocal

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var db: AppDatabase;
    private val _histories = MutableLiveData<List<HistoryLocal>>().apply {
        // Access a Cloud Firestore instance from your Activity
        db = AppDatabase(application)
        val histories =  db.storyDao().getAllHistoryLocal()
        value = histories
    }
    var histories: LiveData<List<HistoryLocal>> = _histories

    /**
     * Navigation for the SleepDetail fragment.
     */
    private val _history = MutableLiveData<HistoryLocal?>()
    val navigateToHistory
        get() = _history

    fun onHistoryClicked(id: Long) {
    }

    fun onHistoryNavigated() {
        _history.value = null
    }
}