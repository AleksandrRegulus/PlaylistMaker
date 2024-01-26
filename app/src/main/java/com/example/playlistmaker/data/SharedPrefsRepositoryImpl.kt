package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.domain.api.SharedPrefsRepository
import com.example.playlistmaker.domain.search.model.Track
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SharedPrefsRepositoryImpl(
    private var sharedPrefs: SharedPreferences,
    private val gson: Gson,
    private val appDatabase: AppDatabase,
): SharedPrefsRepository {

    override fun getThemeFromSharedPrefs (): Boolean {
        return sharedPrefs.getBoolean(DARK_THEME_KEY, false)
    }

    override fun saveThemeToSharedPrefs(theme: Boolean) {
        sharedPrefs.edit()
            .putBoolean(DARK_THEME_KEY, theme)
            .apply()
    }

    override fun getSearchHistoryFromSharedPrefs(): Flow<List<Track>> = flow {
        val json = sharedPrefs.getString(SEARCH_HISTORY_KEY, null)
        val historyTracks = if (json.isNullOrEmpty()) emptyList()
            else gson.fromJson(json, Array<Track>::class.java).toList()
        val favTracks = appDatabase.favTrackDao().getFavTracksIDs()
        emit(historyTracks.map {
            it.copy(isFavorite = it.trackId in favTracks)
        })
    }

    override fun saveHistoryToSharedPrefs(history: String) {
        sharedPrefs.edit()
            .putString(SEARCH_HISTORY_KEY, history)
            .apply()
    }
    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history"
        private const val DARK_THEME_KEY = "dark_theme"
    }


}