package com.smarttech.story.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.smarttech.story.dao.StoryDao
import com.smarttech.story.dao.StoryDescDao
import com.smarttech.story.model.*
import com.smarttech.story.model.local.Bookmark
import com.smarttech.story.model.local.Download
import com.smarttech.story.model.local.History

@Database(
    entities = arrayOf(
        StoryDesc::class
    ),
    version = 1
)
abstract class DescDatabase : RoomDatabase() {
    abstract fun storyDescDao(): StoryDescDao

    companion object {
        @Volatile
        private var instance: DescDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            DescDatabase::class.java, "story_desc.db"
        ).allowMainThreadQueries()
            .build()
    }
}