package com.smarttech.story.utils

import android.content.Context
import android.content.Intent
import com.smarttech.story.MainActivity
import com.smarttech.story.constants.Constants
import com.smarttech.story.networking.DropboxService
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class NumberUtil {
    companion object {
        fun formatInt(number: Int): String {
            val dec = DecimalFormat("#,###.##")
            return dec.format(number)
        }
    }
}