package com.example.playlistmaker.util

import java.text.SimpleDateFormat
import java.util.Locale

object DateTimeUtil {

    fun millisToTimeFormat(millis: Long): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(millis)
    }
}