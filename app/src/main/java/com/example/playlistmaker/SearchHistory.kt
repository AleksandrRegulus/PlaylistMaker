package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

private const val MAX_TRACKS_IN_HISTORY = 10
class SearchHistory(val sharedPrefs: SharedPreferences) {
    // чтение
    fun readHistory(): ArrayList<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY, null) ?: return ArrayList()
        val result = ArrayList<Track>()
        result.addAll(Gson().fromJson(json, Array<Track>::class.java))
        return result
    }

    fun addTrackToHistoy (track: Track) {
        val readTracks = readHistory()
        val position = readTracks.indexOf(track)
        if (position == -1) {
            if (readTracks.size == MAX_TRACKS_IN_HISTORY) readTracks.removeLast()
        } else {
            readTracks.removeAt(position)
        }
        readTracks.add(0, element = track)
        saveHistory(readTracks)
    }

    // запись
    fun saveHistory(historyTracks: ArrayList<Track>) {
            val json = Gson().toJson(historyTracks)
            sharedPrefs.edit()
                .putString(SEARCH_HISTORY, json)
                .apply()
    }
}