package com.smarttech.story.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryStories(@PrimaryKey var categoryId: Long, var storyId: Long)
