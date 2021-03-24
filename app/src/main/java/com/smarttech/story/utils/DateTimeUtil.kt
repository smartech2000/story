package com.smarttech.story.utils

import android.content.Context
import android.content.Intent
import com.smarttech.story.MainActivity
import com.smarttech.story.constants.Constants
import com.smarttech.story.networking.DropboxService
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DateTimeUtil {
    companion object {
        fun getCurrentTime(): String {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val currentDateAndTime: String = simpleDateFormat.format(Date())
            return  currentDateAndTime
        }
    }
}