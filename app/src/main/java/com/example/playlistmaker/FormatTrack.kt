package com.example.playlistmaker

import java.text.SimpleDateFormat
import java.util.Locale

object FormatTrack  {

        const val DEFAULT_TIME_STRING = "00:00"

        fun getTimeFromMillis(trackTimeMillis: String): String {
            val trackTimeInMilliseconds = trackTimeMillis.toLongOrNull()
            return if (trackTimeInMilliseconds != null) {
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeInMilliseconds)
            } else DEFAULT_TIME_STRING
        }

        fun getCoverArtwork(artworkUrl100: String?) =
            if (artworkUrl100.isNullOrEmpty()) "" else artworkUrl100.replaceAfterLast( // получаем ссылку на большой постер
                '/',
                "512x512bb.jpg"
            )

        fun getYearFromReleaseDate(releaseDate: String?) =
            if (releaseDate.isNullOrEmpty()) "" else releaseDate.substring(0..3)
    }
