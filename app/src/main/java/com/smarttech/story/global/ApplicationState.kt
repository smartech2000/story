package com.smarttech.story.global

import java.util.concurrent.atomic.AtomicBoolean

object ApplicationState {
    var storyDescDbDone: AtomicBoolean = AtomicBoolean(false)
}