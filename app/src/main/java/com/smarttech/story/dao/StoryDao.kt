package com.smarttech.story.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.smarttech.story.model.Category
import com.smarttech.story.model.Story
import com.smarttech.story.model.dto.StoryViewInfo
import com.smarttech.story.model.local.Bookmark
import com.smarttech.story.model.local.Download
import com.smarttech.story.model.local.History

@Dao
interface StoryDao {
    @Query("SELECT * FROM Category")
    fun getAllCategory(): List<Category>

    @Query(
        "SELECT Story.*, Status.title as statusTitle, Author.name as authorTitle FROM Story , Status, Author" +
                " where Story.author_id = Author.id and Story.status = Status.id and " +
                "( lower(story.title) like lower(:keySearch) or lower((select author.name from author where story.author_id =  author.id)) like lower(:keySearch))"
    )
    fun searchStory(keySearch: String): List<StoryViewInfo>

    @Query("SELECT Story.*, Status.title as statusTitle, Author.name as authorTitle FROM Story , Status, Author " +
            "where Story.author_id = Author.id and Story.status = Status.id and  Story.id in (select story_id from CATEGORY_STORY where category_id=:categoryId)")
    fun getStoryByCategoryId(categoryId: Int): List<StoryViewInfo>

    @Query(
        "SELECT Story.*, Status.title as statusTitle, Author.name as authorTitle " +
                "FROM Story , Status, Author where Story.id=:storyId and  Story.author_id = Author.id and Story.status = Status.id"
    )
    fun getStoryById(storyId: Int): StoryViewInfo


    @Query("SELECT * FROM story  where id =:storyId")
    fun findStoryById(storyId: Int): Story

    @Query("SELECT Story.*, Status.title as statusTitle, Author.name as authorTitle " +
            "FROM Story , History, Status, Author " +
            "where Story.id = History.story_id and Story.author_id = Author.id and Story.status = Status.id ")
    fun findAllHistory(): List<StoryViewInfo>

    @Query("SELECT * FROM History")
    fun getAllHistoryLocal(): List<History>

    @Query("SELECT * FROM Download")
    fun getAllDownloadLocal(): List<Download>

    @Query("SELECT * FROM Bookmark")
    fun getAllBookmarkLocal(): List<Bookmark>

    @Insert
    fun insertHistoryLocal(vararg historyLocal: History)

    @Insert
    fun insertDownloadLocal(vararg downloadLocal: Download)

    @Insert
    fun insertBookmarkLocal(vararg bookmarkLocal: Bookmark)
}