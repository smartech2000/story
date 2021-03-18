package com.smarttech.story.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "bookmark")
data class Bookmark(
    @PrimaryKey val id: Int,
    val story_id: Int?,
    val create_time: String?
)
