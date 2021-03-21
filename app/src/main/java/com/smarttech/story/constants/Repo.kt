package com.smarttech.story.constants

import java.io.File

enum class Repo(name: String) {
    STORY("stories"),
    CHAPTER("chapters"),
    AVATAR("avatars");

    fun getRepo(rootDir: File): File {
        val dir = File(rootDir, name)
        if (!dir.exists()){
            dir.mkdirs()
        }
        return dir
    }

    fun getUri(key: String, id: Int): String {
        return Constants.DROPBOX_URL.replace("{shareKey}", key)
            .replace(
                "{fileName}",
                "$id"
            )
    }
}