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

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var db: AppDatabase
    private val _categories = MutableLiveData<List<Category>>().apply {
        val storyDao = AppDatabase(application).storyDao()
        val storyDescDao = DescDatabase(application).storyDescDao()
        value = storyDao.getAllCategory()

        if (!FileUtil.databaseFileExists(application, "story_desc.db")) {
            GlobalScope.launch(Dispatchers.IO) {
                FileUtil.attachDbFromDropBox(application, "uyjrl187170adjr", "story_desc")
                val x = 0
/*            launch { // chapter count
                val url = "https://www.dropbox.com/s/ztw2jimk6duitv4/story_chapter?dl=1"
                var stringResponse:String?
                val response = DropboxService.getInstance().downlload(url).execute()
                val body = response.body()
                stringResponse = body?.bytes()?.let { UnzipUtility.ungzip(it) }
                val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
                val type = Types.newParameterizedType(List::class.java, ChapterCountDto::class.java)
                val adapter = moshi.adapter<List<ChapterCountDto>>(type);
                val chapterCountDtos = adapter.fromJson(stringResponse)

          *//*      chapterCountDtos.forEach {

                }*//*
                val x = 0;

            }*/
/*            launch {//story desc
                val url = Constants.DROPBOX_URL.replace("{shareKey}","adk0s9vgcznfch0").replace("{fileName}","story_desc")
                var stringResponse:String?
                val response = DropboxService.getInstance().downlload(url).execute()
                val body = response.body()
                stringResponse = body?.bytes()?.let { UnzipUtility.ungzip(it) }
                var type = Types.newParameterizedType(MutableMap::class.java, String::class.java, String::class.java)

                val moshi = Moshi.Builder()
                    //.add(JsonAdapter<t>)
                    .build()

                val adapter = moshi.adapter<MutableMap<String, String>>(type);

                var map = adapter.fromJson(stringResponse);
                val x = 0

            }*/

            }
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
/*        db.collection("category").whereEqualTo("id", id).get()
            .addOnSuccessListener { docs ->
                _category.value = docs.documents.get(0).toObject(Category::class.java)
            }*/
    }

    fun onCategoryNavigated() {
        _category.value = null
    }
}