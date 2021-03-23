package com.smarttech.story.ui.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.smarttech.story.constants.Constants
import com.smarttech.story.database.AppDatabase
import com.smarttech.story.database.DescDatabase
import com.smarttech.story.global.ApplicationState
import com.smarttech.story.model.Category
import com.smarttech.story.model.dto.ChapterCountDto
import com.smarttech.story.networking.DropboxService
import com.smarttech.story.utils.FileUtil
import com.smarttech.story.utils.UnzipUtility
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var db: AppDatabase
    private val _categories = MutableLiveData<List<Category>>().apply {
        val storyDao = AppDatabase(application).storyDao()
        val storyDescDao = DescDatabase(application).storyDescDao()
        value = storyDao.getAllCategory()

        if (!FileUtil.databaseFileExists(application, "story_desc.db")) {
            GlobalScope.launch(Dispatchers.IO) {
                FileUtil.attachDbFromDropBox(application, "uyjrl187170adjr", "story_desc")
                ApplicationState.storyDescDbDone = AtomicBoolean(true)
                val x = 0
            }

        } else {
            ApplicationState.storyDescDbDone = AtomicBoolean(true)
        }


        var x = 0;
    }
    var categories: LiveData<List<Category>> = _categories

    /**
     * Navigation for the SleepDetail fragment.
     */
    private val _category = MutableLiveData<Category?>()
    val navigateToCategory
        get() = _category

    fun onCategoryClicked(id: Int) {
        _category.value = _categories.value?.filter { category -> category.id == id }?.last()
    }

    fun onCategoryNavigated() {
        _category.value = null
    }
}