package com.smarttech.story.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class CategoryWithStories(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "storyId",
        associateBy = Junction(CategoryStories::class)
    )
    val stories:  List<Story>
)
