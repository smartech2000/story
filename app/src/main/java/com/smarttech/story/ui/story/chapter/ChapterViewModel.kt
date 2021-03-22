package com.smarttech.story.ui.story.chapter

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.smarttech.story.constants.Constants
import com.smarttech.story.constants.Repo
import com.smarttech.story.database.AppDatabase
import com.smarttech.story.model.dto.ChapterDto
import com.smarttech.story.model.dto.StoryViewInfo
import com.smarttech.story.networking.DropboxService
import com.smarttech.story.utils.UnzipUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ChapterViewModel(
    application: Application,
    context: Context,
    private val storyId: Int,
    private val chapterKey: String,
    private val chapterIndex: Int
) : AndroidViewModel(application) {
    private val _chapterContent = MutableLiveData<String>().apply {
        val repo: Repo = Repo.CHAPTER
        GlobalScope.launch(Dispatchers.IO) {
            launch {
                val storyDir = File(repo.getRepo(context.cacheDir),"$storyId")
                if (!storyDir.exists()){
                    storyDir.mkdirs()
                }
                val dataFile = File(storyDir, "$chapterIndex")
                var b: ByteArray?
                if (dataFile.exists()) {
                    b = dataFile.readBytes()
                } else {
                    val url = repo.getUri(
                        "$chapterKey",
                        "$chapterIndex"
                    )
                    val response = DropboxService.getInstance().downlload(url).execute()
                    val body = response.body()
                    b = body?.bytes()
                    if (b != null) {
                        dataFile.writeBytes(b)
                    }
                }
                val stringResponse = b?.let { UnzipUtility.ungzip(it) }
                withContext(Dispatchers.Main) {
                    value = stringResponse
                }

            }
            val x = 0;
        }
    }
    var chapterContent: LiveData<String> = _chapterContent
}