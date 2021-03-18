package com.smarttech.story.model.dto

import androidx.room.Embedded
import com.smarttech.story.model.Story

class StoryViewInfo(
    @Embedded val story: Story,
    val statusTitle: String,
    val authorTitle: String
)