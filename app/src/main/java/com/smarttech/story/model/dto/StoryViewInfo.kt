package com.smarttech.story.model.dto

import android.graphics.Bitmap
import androidx.room.Embedded
import com.smarttech.story.model.Story

class StoryViewInfo(
    @Embedded val story: Story,
    val statusTitle: String,
    var authorTitle: String
)