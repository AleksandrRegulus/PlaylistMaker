package com.example.playlistmaker.util

object StringUtil {

    fun getNumTracksString(num: Int): String {
        val ends = arrayOf("трек", "трека", "треков")
        return if ((num % 100) in 11..19) ends[2]
        else when (num % 10) {
            1 -> ends[0]
            in 2..4 -> ends[1]
            else -> ends[2]
        }
    }
}