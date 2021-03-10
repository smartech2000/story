package com.smarttech.story.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HistoryLocal(
    @PrimaryKey val id: Long,
    val cloudId: Long,
    val name: String,
    val author: String
)
