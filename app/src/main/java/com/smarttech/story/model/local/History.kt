package com.smarttech.story.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "history")
data class History(
    @PrimaryKey val id: Int,
    val story_id: Int?,
    val create_time: String?
)
