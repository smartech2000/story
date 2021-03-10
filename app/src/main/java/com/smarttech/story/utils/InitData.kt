package com.smarttech.story.utils

import com.smarttech.story.dao.StoryDao
import com.smarttech.story.model.local.BookmarkLocal
import com.smarttech.story.model.local.DownloadLocal
import com.smarttech.story.model.local.HistoryLocal

class InitData {

    open fun InitData(storyDao: StoryDao) {
        if (storyDao.getAllBookmarkLocal().size ==0) {
            val historyLocal = HistoryLocal(1, 1, "HistoryLocal Name 1", "HistoryLocal Author1")
            val downloadLocal = DownloadLocal(1, 1, "DownloadLocal Name 1", "DownloadLocal Author1")
            val bookmarkLocal = BookmarkLocal(1, 1, "BookmarkLocal Name 1", "BookmarkLocal Author1")
            storyDao.insertHistoryLocal(historyLocal)
            storyDao.insertDownloadLocal(downloadLocal)
            storyDao.insertBookmarkLocal(bookmarkLocal)
        }
    }
}