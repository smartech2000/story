package com.smarttech.story.ui.story.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseReference
import com.smarttech.story.database.AppDatabase
import com.smarttech.story.model.*
import com.smarttech.story.model.dto.ChapterDto
import com.smarttech.story.model.dto.StoryViewInfo
import com.smarttech.story.networking.DropboxService
import com.smarttech.story.utils.UnzipUtility
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoryDetailViewModel(
    application: Application,
    private val storyId: Int,
    private val storyName: String
) : AndroidViewModel(application) {
    private lateinit var database: DatabaseReference
    private lateinit var db: AppDatabase
    private val _story = MutableLiveData<StoryViewInfo>().apply {
        val storyDao = AppDatabase(application).storyDao()
        value = storyDao.getStoryById(storyId)

    }
    var story: LiveData<StoryViewInfo> = _story


    private val _chapterDtos = MutableLiveData<List<ChapterDto>>().apply {
        val storyDao = AppDatabase(application).storyDao()
        GlobalScope.launch(Dispatchers.IO) {
            launch { // chapter count
                val story = storyDao.findStoryById(storyId)
                val url = "https://www.dropbox.com/s/${story.dropboxUri}/story_chapter?dl=1"
                var stringResponse: String?
                val response = DropboxService.getInstance().downlload(url).execute()
                val body = response.body()
                stringResponse = body?.bytes()?.let { UnzipUtility.ungzip(it) }
                val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
                val type = Types.newParameterizedType(List::class.java, ChapterDto::class.java)
                val adapter = moshi.adapter<List<ChapterDto>>(type);
                var chapterCountDtos = adapter.fromJson(stringResponse)
                chapterCountDtos=  chapterCountDtos?.sortedBy { it.index }
                withContext(Dispatchers.Main) {
                    value = chapterCountDtos
                }

            }
            val x = 0;

        }


    }


    var chapterDtos: LiveData<List<ChapterDto>> = _chapterDtos

    /**
     * Navigation for the SleepDetail fragment.
     */
    private val _navigateToChapter = MutableLiveData<ChapterDto?>()
    val navigateToChapter
        get() = _navigateToChapter

    fun onChapterClicked(chapterDto: ChapterDto) {
        _navigateToChapter.value = chapterDto

    }

    fun onChapterNavigated() {
        _navigateToChapter.value = null
    }
}
