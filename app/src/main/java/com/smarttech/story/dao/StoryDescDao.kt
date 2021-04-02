package com.smarttech.story.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.smarttech.story.model.Category
import com.smarttech.story.model.Story
import com.smarttech.story.model.StoryDesc
import com.smarttech.story.model.dto.StoryViewInfo
import com.smarttech.story.model.local.Bookmark
import com.smarttech.story.model.local.Download
import com.smarttech.story.model.local.History

@Dao
interface StoryDescDao {
    @Query("SELECT * FROM story_desc where id=:storyId ")
    fun findStoryDescById(storyId: Int): StoryDesc?

}