package com.smarttech.story.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.smarttech.story.dao.StoryDao
import com.smarttech.story.model.*
import com.smarttech.story.model.local.Bookmark
import com.smarttech.story.model.local.Download
import com.smarttech.story.model.local.History

@Database(
    entities = arrayOf(
        Category::class,
        Story::class, Status::class,Author::class,
        CategoryStories::class,History::class,  Download::class, Bookmark::class),
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