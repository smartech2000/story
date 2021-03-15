package com.smarttech.story.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Status(@PrimaryKey  @ColumnInfo(name = "id") var id: Int, @ColumnInfo(name = "title") var title: String?)
