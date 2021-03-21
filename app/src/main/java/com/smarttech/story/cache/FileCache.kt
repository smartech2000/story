package com.smarttech.story.cache

import android.content.Context
import android.os.Environment
import java.io.File

class FileCache(context: Context) {
    private var cacheDir: File? = null
    fun getFile(url: String): File {
        val filename = url.hashCode().toString()
        return File(cacheDir, filename)
    }
    fun clear() {
        val files = cacheDir!!.listFiles() ?: return
        for (f in files) f.delete()
    }

    init {
        //Find the dir to save cached images
        cacheDir = context.cacheDir
        if (!cacheDir!!.exists()) cacheDir!!.mkdirs()
    }
}