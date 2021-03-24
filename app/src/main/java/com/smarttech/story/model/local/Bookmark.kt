package com.smarttech.story.model.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "bookmark")
data class Bookmark(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "story_id") val storyId: Int?,
    @ColumnInfo(name = "chap_index") val chapterIndex: Int?,
    @ColumnInfo(name = "page") val page: Int?,
    @ColumnInfo(name = "create_time") val createTime: String?
)
