package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

class SearchHistory(val sharedPrefs: SharedPreferences) {
    // чтение
    fun readHistory(): ArrayList<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY, null) ?: return ArrayList()
        val result = ArrayList<Track>()
        result.addAll(Gson().fromJson(json, Array<Track>::class.java))
        return result
    }

    // запись
    fun saveHistory(historyTracks: ArrayList<Track>) {
            val json = Gson().toJson(historyTracks)
            sharedPrefs.edit()
                .putString(SEARCH_HISTORY, json)
                .apply()
    }
}