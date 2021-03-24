package com.smarttech.story.model.local

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.smarttech.story.utils.DateTimeUtil

@Entity(tableName = "history")
data class History(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "story_id") val storyId: Int?,
    @ColumnInfo(name = "chap_index") val chapterIndex: Int?,
    @ColumnInfo(name = "page") val page: Int?,
    @ColumnInfo(name = "create_time") val createTime: String?


) {
    constructor(storyId: Int?) : this(0,
        storyId,
        0,
        0,
        createTime = DateTimeUtil.getCurrentTime())
}
