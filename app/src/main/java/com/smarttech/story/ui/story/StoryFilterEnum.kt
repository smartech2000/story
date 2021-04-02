package com.smarttech.story.ui.story

enum class StoryFilterEnum(val filter: Int) {
    UPDATE(1),
    RATE(2),
    VIEW(3),
    CHAPTER(4),
    NAME(5);


    companion object {
        operator fun invoke(rawValue: Int) = StoryFilterEnum.values().find { it.filter == rawValue }
    }
}