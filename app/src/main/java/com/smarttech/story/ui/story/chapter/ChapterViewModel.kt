package com.smarttech.story.ui.story.chapter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.smarttech.story.database.AppDatabase
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

class ChapterViewModel(
    application: Application,
    private val chapterKey: String,
    private val chapterIndex: Int
) : AndroidViewModel(application) {
    private val _chapterContent = MutableLiveData<String>().apply {
        GlobalScope.launch(Dispatchers.IO) {
            launch { // chapter count
                //https://www.dropbox.com/s/qiror7fgeezcosg/341.o?dl=1%E2%80%8B%E2%80%8B%E2%80%8B%E2%80%8B%E2%80%8B%E2%80%8B%E2%80%8B
                val url = "https://www.dropbox.com/s/${chapterKey}/${chapterIndex}.o?dl=1"
                var stringResponse: String?
                val response = DropboxService.getInstance().downlload(url).execute()
                val body = response.body()
                stringResponse = body?.bytes()?.let { UnzipUtility.ungzip(it) }
                withContext(Dispatchers.Main) {
                    value = stringResponse
                }

            }
            val x = 0;

        }
    }
    var chapterContent: LiveData<String> = _chapterContent
}