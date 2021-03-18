package com.smarttech.story.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "story")
data class Story(
    @PrimaryKey   @ColumnInfo(name = "id") var id: Int,
    @ColumnInfo(name = "source") var source: String?,
    @ColumnInfo(name = "avatar") var avatar: String?,
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "dataDir") var dataDir: String?,
    @ColumnInfo(name = "dropbox_uri") var dropboxUri: String?,
    @ColumnInfo(name = "view") var view: String?,
    @ColumnInfo(name = "rate") var rate: Double?,
    @ColumnInfo(name = "chap_num") var chapNum: Int?,
    @ColumnInfo(name = "updated_date") var updateDate: String?,
    @ColumnInfo(name = "author_id") var authorId: Int?,
    @ColumnInfo(name = "uploaded_by") var uploadedBy: Int?,
    @ColumnInfo(name = "status") var status: Int?
)
