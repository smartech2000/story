package com.smarttech.story.model

import androidx.room.Embedded

class StoryViewInfo(
    @Embedded val story: Story,
    val statusTitle: String,
    val authorTitle: String
)