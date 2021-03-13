package com.smarttech.story.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(@PrimaryKey var id: Long =0, var title:String ="")
