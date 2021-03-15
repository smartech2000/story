package com.smarttech.story.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "story")
data class Story(
    @PrimaryKey   @ColumnInfo(name = "id") var storyId: Int,
    @ColumnInfo(name = "source") var source: String?,
    @ColumnInfo(name = "avatar") var avatar: String?,
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "dataDir") var dataDir: String?,
    @ColumnInfo(name = "md5Key") var md5key: String?,
    @ColumnInfo(name = "href") var href: String?,
    @ColumnInfo(name = "description") var description: String?,

    @ColumnInfo(name = "view") var view: String?,

    @ColumnInfo(name = "rate") var rate: Double?,
    @ColumnInfo(name = "author_id") var authorId: Int?,
    @ColumnInfo(name = "status") var status: Int?

)
