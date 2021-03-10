package com.smarttech.story.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.smarttech.story.model.local.BookmarkLocal
import com.smarttech.story.model.local.DownloadLocal
import com.smarttech.story.model.local.HistoryLocal

@Dao
interface StoryDao {
    @Query("SELECT * FROM HistoryLocal")
    fun getAllHistoryLocal(): List<HistoryLocal>

    @Query("SELECT * FROM DownloadLocal")
    fun getAllDownloadLocal(): List<DownloadLocal>

    @Query("SELECT * FROM BookmarkLocal")
    fun getAllBookmarkLocal(): List<BookmarkLocal>

    @Insert
    fun insertHistoryLocal(vararg historyLocal: HistoryLocal)

    @Insert
    fun insertDownloadLocal(vararg downloadLocal: DownloadLocal)

    @Insert
    fun insertBookmarkLocal(vararg bookmarkLocal: BookmarkLocal)
}