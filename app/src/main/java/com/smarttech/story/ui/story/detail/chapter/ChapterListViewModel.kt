package com.smarttech.story.ui.story.detail.chapter

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.smarttech.story.constants.Repo
import com.smarttech.story.database.AppDatabase
import com.smarttech.story.database.DescDatabase
import com.smarttech.story.global.ApplicationState
import com.smarttech.story.model.dto.ChapterDto
import com.smarttech.story.networking.DropboxService
import com.smarttech.story.utils.UnzipUtility
import kotlinx.coroutines.*
import java.io.File

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
            launch { // chapter count
                val story = storyDao.findStoryById(storyId)
                val dataFile =
                    File(repo.getRepo(context.cacheDir), "$storyId")

                var b: ByteArray?
                if (dataFile.exists()) {
                    b = dataFile.readBytes()
                } else {
                    val url = repo.getUri("${story.dropboxUri}", "story.o3")
                    val response = DropboxService.getInstance().downlload(url).execute()
                    val body = response.body()
                    b = body?.bytes()
                    if (b != null) {
                        dataFile.writeBytes(b)
                    }
                }
                if (b != null) {
                    val stringResponse = b.let { UnzipUtility.ungzip(it) }

                    var chapterCountDtos = Gson().fromJson(stringResponse, Array<ChapterDto>::class.java).asList()
                    chapterCountDtos = chapterCountDtos?.sortedBy { it.index }
                    withContext(Dispatchers.Main) {
                        value = chapterCountDtos
                    }
                }
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