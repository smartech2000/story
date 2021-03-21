package com.smarttech.story.cache

import android.graphics.Bitmap
import android.util.Log
import java.util.*

class MemoryCache {
    private val cache = Collections.synchronizedMap(
        LinkedHashMap<Int, Bitmap>(10, 1.5f, true)
    )
    private var size: Long = 0
    private var limit: Long = 1000000
    fun setLimit(new_limit: Long) {
        limit = new_limit
        Log.i(TAG, "MemoryCache will use up to " + limit / 1024.0 / 1024.0 + "MB")
    }

    operator fun get(id: Int?): Bitmap? {
        return try {
            if (!cache.containsKey(id)) null else cache[id]
        } catch (ex: NullPointerException) {
            ex.printStackTrace()
            null
        }
    }

    fun put(id: Int, bitmap: Bitmap?) {
        try {
            if (cache.containsKey(id)) size -= getSizeInBytes(cache[id])
            cache[id] = bitmap
            size += getSizeInBytes(bitmap)
            checkSize()
        } catch (th: Throwable) {
            th.printStackTrace()
        }
    }

    private fun checkSize() {
        Log.i(TAG, "cache size=" + size + " length=" + cache.size)
        if (size > limit) {
            val iter: MutableIterator<Map.Entry<Int, Bitmap?>> =
                cache.entries.iterator()
            while (iter.hasNext()) {
                val entry = iter.next()
                size -= getSizeInBytes(entry.value)
                iter.remove()
                if (size <= limit) break
            }
            Log.i(TAG, "Clean cache. New size " + cache.size)
        }
    }

    fun clear() {
        try {
            cache.clear()
            size = 0
        } catch (ex: NullPointerException) {
            ex.printStackTrace()
        }
    }

    fun getSizeInBytes(bitmap: Bitmap?): Long {
        return if (bitmap == null) 0 else (bitmap.rowBytes * bitmap.height).toLong()
    }

    companion object {
        private const val TAG = "MemoryCache"
    }

    init {
        setLimit(Runtime.getRuntime().maxMemory() / 4)
    }
}