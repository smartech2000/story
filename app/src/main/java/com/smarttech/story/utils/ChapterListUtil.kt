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

class ChapterListUtil {
    companion object {

        fun bindFromLocal(ctx: Context, imgView: ImageView, storyId: Int) {
            var bmp = MemoryCache.getInstance().get(storyId)
            if (bmp == null) {
                val avatarFile = File(Repo.AVATAR.getRepo(ctx.cacheDir), "$storyId")
                if (avatarFile.exists()) {
                    val b = avatarFile.readBytes()
                    bmp = BitmapFactory.decodeByteArray(b, 0, b.size)
                }
            }
            if (bmp != null) {
                imgView.setImageBitmap(bmp)
            }
        }

        fun getChapterListFromServer(context: Context, storyId: Int): List<ChapterDto> {
            val storyDao = AppDatabase(context).storyDao()
            val story = storyDao.findStoryById(storyId)
            var chapterDtos: List<ChapterDto> = emptyList()
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
            return chapterDtos
        }

    }
}