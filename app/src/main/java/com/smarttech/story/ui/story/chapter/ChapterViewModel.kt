package com.smarttech.story.ui.story.chapter

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.smarttech.story.constants.Repo
import com.smarttech.story.utils.ChapterUtil
import com.smarttech.story.utils.UnzipUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChapterViewModel(
    application: Application,
    context: Context,
    private val storyId: Int,
    private val chapterKey: String,
    private val chapterIndex: Int
) : AndroidViewModel(application) {
    private val _chapterContent = MutableLiveData<String>().apply {
        val repo: Repo = Repo.CHAPTER
        if (chapterIndex !=-1) {
            GlobalScope.launch(Dispatchers.IO) {
                val b = ChapterUtil.downloadChapterFromServer(context,
                    storyId,
                    chapterIndex,
                    chapterKey)

                val stringResponse = b?.let { UnzipUtility.ungzip(it) }
                withContext(Dispatchers.Main) {
                    value = stringResponse
                }

                val x = 0;
            }
        } else {

            GlobalScope.launch(Dispatchers.IO) {
                val chapterDtos =
                    context?.let { it1 -> ChapterUtil.getChapterListFromServer(it1, storyId) }
                if (chapterDtos.isNotEmpty()) {
                    val chapterDto = chapterDtos.get(0)
                    val b = ChapterUtil.downloadChapterFromServer(context,
                        storyId,
                        chapterDto.index,
                        chapterDto.key)

                    val stringResponse = b?.let { UnzipUtility.ungzip(it) }
                    withContext(Dispatchers.Main) {
                        value = stringResponse
                    }
                }
            }


        }
    }
    var chapterContent: LiveData<String> = _chapterContent
}