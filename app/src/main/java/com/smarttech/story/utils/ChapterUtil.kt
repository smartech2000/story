package com.smarttech.story.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.google.gson.Gson
import com.smarttech.story.MainActivity
import com.smarttech.story.cache.MemoryCache
import com.smarttech.story.constants.Constants
import com.smarttech.story.constants.Repo
import com.smarttech.story.database.AppDatabase
import com.smarttech.story.model.Story
import com.smarttech.story.model.dto.ChapterCountDto
import com.smarttech.story.model.dto.ChapterDto
import com.smarttech.story.networking.DropboxService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*

class ChapterUtil {
    companion object {

        fun downloadChapterFromServer(context: Context, storyId:Int, chapterIndex: Int, chapterKey:String): ByteArray?{
            var b: ByteArray?=null
            try {
                val repo: Repo = Repo.CHAPTER
                val storyDir = File(repo.getRepo(context.cacheDir),"$storyId")
                if (!storyDir.exists()){
                    storyDir.mkdirs()
                }
                val dataFile = File(storyDir, "$chapterIndex")

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
            } catch (e: Exception) {
            }
            return  b
        }

        fun getChapterListFromServer(context: Context, storyId: Int): List<ChapterDto> {
            var chapterDtos: List<ChapterDto> = emptyList()
            try {
                val storyDao = AppDatabase(context).storyDao()
                val story = storyDao.findStoryById(storyId)

                val repo: Repo = Repo.STORY
                val dataFile =
                    File(repo.getRepo(context.cacheDir), story.id.toString())

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

                    chapterDtos =
                        Gson().fromJson(stringResponse, Array<ChapterDto>::class.java).asList()
                    chapterDtos = chapterDtos?.sortedBy { it.index }
                }
            } catch (e: Exception) {
            }
            return chapterDtos
        }

    }
}