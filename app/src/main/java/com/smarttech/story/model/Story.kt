package com.smarttech.story.model

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class Story(
    @PrimaryKey var id: Long = 0,
    var authorId: Long = 0,
    var avatar: String = "",
    var description: String = "",
    var rate: Double,
    var source: String,
    var status: Int,
    var title: String,
    var view: Long
)
