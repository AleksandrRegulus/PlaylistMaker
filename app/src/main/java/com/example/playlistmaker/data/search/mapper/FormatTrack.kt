package com.example.playlistmaker.data.search.mapper

import com.example.playlistmaker.R
import com.example.playlistmaker.util.DateTimeUtil

object FormatTrack {

    fun getTimeFromMillis(trackTimeMillis: String): String {
        val trackTimeInMilliseconds = trackTimeMillis.toLongOrNull()
        return if (trackTimeInMilliseconds != null) {
            DateTimeUtil.millisToTimeFormat(trackTimeInMilliseconds)
        } else R.string.default_time.toString()
    }

    fun getCoverArtwork(artworkUrl100: String?) =
        if (artworkUrl100.isNullOrEmpty()) "" else artworkUrl100.replaceAfterLast( // получаем ссылку на большой постер
            '/',
            "512x512bb.jpg"
        )

    fun getYearFromReleaseDate(releaseDate: String?) =
        if (releaseDate.isNullOrEmpty()) "" else releaseDate.substring(0..3)
}
