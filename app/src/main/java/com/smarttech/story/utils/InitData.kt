package com.smarttech.story.utils

import com.smarttech.story.dao.StoryDao
import com.smarttech.story.model.local.Bookmark
import com.smarttech.story.model.local.Download
import com.smarttech.story.model.local.History

class InitData {

    open fun InitData(storyDao: StoryDao) {
        if (storyDao.getAllBookmarkLocal().size ==0) {
/*            val historyLocal = History(1, 1, "History Name 1", "History Author1")
            val downloadLocal = Download(1, 1, "Download Name 1", "Download Author1")
            val bookmarkLocal = Bookmark(1, 1, "Bookmark Name 1", "Bookmark Author1")
            storyDao.insertHistoryLocal(historyLocal)
            storyDao.insertDownloadLocal(downloadLocal)
            storyDao.insertBookmarkLocal(bookmarkLocal)*/
        }
    }
}