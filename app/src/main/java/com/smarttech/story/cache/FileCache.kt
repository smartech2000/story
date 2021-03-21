package com.smarttech.story.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.smarttech.story.constants.Constants
import com.smarttech.story.constants.Repo
import com.smarttech.story.networking.DropboxService
import com.smarttech.story.ui.story.StoryRecyclerViewAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

class FileCache(private val context: Context, private val repo: Repo) {
    private val dataDir by lazy {
        val dir = repo.getRepo(getRootDir())
        if (!dir.exists()) {
            dir.mkdirs()
        }
        dir
    }

    fun getRootDir(): File {
        return context.cacheDir
    }

    fun getBuffer(key: String, id: Int, onCompleted: (ByteArray?) -> Unit) {
        val dataFile = File(dataDir, "$id")
        if (dataFile.exists()) {
            GlobalScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Main) {
                    onCompleted(dataFile.readBytes())
                }
            }
        } else {
            loadFromInternet(key, id, onCompleted)
        }
    }

    fun loadFromInternet(key: String, id: Int, onCompleted: (ByteArray?) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val url = repo.getUri(key, id)
            val b = LoadFromUrl(url)
            if (b != null) {
                val dataFile = File(dataDir, "$id")
                dataFile.writeBytes(b)
            }
            withContext(Dispatchers.Main) {
                onCompleted(b)
            }
        }
    }

    fun LoadFromUrl(url: String): ByteArray? {
        val response = DropboxService.getInstance().downlload(url).execute()
        val body = response.body()
        if (body == null) {
            return null
        }
        val ins: InputStream = body.byteStream()
        val input = BufferedInputStream(ins)
        val output: ByteArrayOutputStream = ByteArrayOutputStream()

        val data = ByteArray(1024)

        var count = 0
        while (input.read(data).also({ count = it }) !== -1) {
            output.write(data, 0, count)
        }

        output.flush()
        output.close()
        input.close()

        return output.toByteArray()
    }
}