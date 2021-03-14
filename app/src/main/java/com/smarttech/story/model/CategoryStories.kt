package com.smarttech.story.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_story")
data class CategoryStories(
    @PrimaryKey(autoGenerate = true) var id: Int?,
    @ColumnInfo(name = "category_id") var categoryId: Int?,
    @ColumnInfo(name = "story_id") var storyId: Int?
)
