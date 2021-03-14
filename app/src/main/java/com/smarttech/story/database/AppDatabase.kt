package com.smarttech.story.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.smarttech.story.dao.StoryDao
import com.smarttech.story.model.Category
import com.smarttech.story.model.CategoryStories
import com.smarttech.story.model.Story
import com.smarttech.story.model.local.BookmarkLocal
import com.smarttech.story.model.local.DownloadLocal
import com.smarttech.story.model.local.HistoryLocal

@Database(
    entities = arrayOf(Category::class,
        Story::class,
        CategoryStories::class,HistoryLocal::class, DownloadLocal::class, BookmarkLocal::class),
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "story.db"
        ).allowMainThreadQueries()
            .build()
    }
}