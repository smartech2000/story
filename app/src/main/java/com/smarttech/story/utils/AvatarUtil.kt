package com.smarttech.story.utils

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.smarttech.story.MainActivity
import com.smarttech.story.cache.MemoryCache
import com.smarttech.story.constants.Constants
import com.smarttech.story.constants.Repo
import com.smarttech.story.networking.DropboxService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*

class AvatarUtil {
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

        fun bindFromServer(ctx: Context, imageView : ImageView, storyId: Int, key: String?) {
            val repo: Repo = Repo.AVATAR
            if (MemoryCache.getInstance().get(storyId) != null) {
                imageView.setImageBitmap(MemoryCache.getInstance().get(storyId))
                return
            }
            // Load avatar
            GlobalScope.launch (Dispatchers.IO) {
                val storyAvatarFile = File(repo.getRepo(ctx.cacheDir), "${key}")
                var b: ByteArray?
                if (storyAvatarFile.exists()) {
                    b = storyAvatarFile.readBytes()
                } else {
                    val url = repo.getUri("${key}", "${storyId}")
                    b = FileUtil.loadFromUrlToBytes(url)
                    if (b != null) {
                        storyAvatarFile.writeBytes(b)
                    }
                }
                if (b != null) {
                    val bmp = BitmapFactory.decodeByteArray(b, 0, b.size)
                    MemoryCache.getInstance().put(storyId, bmp)
                    withContext(Dispatchers.Main) {
                        imageView.setImageBitmap(bmp)
                    }
                }
            }
        }
    }
}