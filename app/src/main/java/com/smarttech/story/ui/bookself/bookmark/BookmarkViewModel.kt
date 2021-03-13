package com.smarttech.story.ui.bookself.bookmark

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import com.smarttech.story.database.AppDatabase
import com.smarttech.story.model.local.BookmarkLocal

class BookmarkViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var db: AppDatabase;
    private val _bookmarks = MutableLiveData<List<BookmarkLocal>>().apply {
        // Access a Cloud Firestore instance from your Activity
        db = AppDatabase(application)
        val bookmarks =  db.storyDao().getAllBookmarkLocal()
        value = bookmarks
    }
    var bookmarks: LiveData<List<BookmarkLocal>> = _bookmarks

    /**
     * Navigation for the SleepDetail fragment.
     */
    private val _bookmark = MutableLiveData<BookmarkLocal?>()
    val navigateToBookmark
        get() = _bookmark

    fun onBookmarkClicked(id: Long) {
    }

    fun onBookmarkNavigated() {
        _bookmark.value = null
    }
}