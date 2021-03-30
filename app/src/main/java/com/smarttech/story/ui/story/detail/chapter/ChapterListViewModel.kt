package com.smarttech.story.ui.story.detail.chapter

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.smarttech.story.constants.Repo
import com.smarttech.story.database.AppDatabase
import com.smarttech.story.database.DescDatabase
import com.smarttech.story.model.dto.ChapterDto
import com.smarttech.story.utils.ChapterUtil
import kotlinx.coroutines.*

class ChapterListViewModel(
    application: Application,
    context: Context,
    private val storyId: Int
) : AndroidViewModel(application) {
    private lateinit var descDb: DescDatabase
    // TODO: Implement the ViewModel
    private val _chapterDtos = MutableLiveData<List<ChapterDto>>().apply {
        val storyDao = AppDatabase(application).storyDao()
        val repo: Repo = Repo.STORY
        GlobalScope.launch(Dispatchers.IO) {
            val chapterDtos = ChapterUtil.getChapterListFromServer(context, storyId)
            withContext(Dispatchers.Main) {
                value = chapterDtos
            }
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