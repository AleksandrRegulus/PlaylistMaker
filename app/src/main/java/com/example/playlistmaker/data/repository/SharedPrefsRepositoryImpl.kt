package com.example.playlistmaker.data.repository

import android.content.Context
import com.example.playlistmaker.domain.api.SharedPrefsRepository

class SharedPrefsRepositoryImpl(private var context: Context): SharedPrefsRepository {

    override fun getThemeFromSharedPrefs (): Boolean {
        val sharedPrefs = context.getSharedPreferences(
            PLAYLIST_MAKER_PREFERENCES,
            Context.MODE_PRIVATE
        )
        return sharedPrefs.getBoolean(DARK_THEME, false)
    }

    override fun saveThemeToSharedPrefs(theme: Boolean) {
        val sharedPrefs = context.getSharedPreferences(
            PLAYLIST_MAKER_PREFERENCES,
            Context.MODE_PRIVATE
        )
        sharedPrefs.edit()
            .putBoolean(DARK_THEME, theme)
            .apply()
    }

    override fun getSearchHistoryFromSharedPrefs(): String? {
        val sharedPrefs = context.getSharedPreferences(
            PLAYLIST_MAKER_PREFERENCES,
            Context.MODE_PRIVATE
        )
        return sharedPrefs.getString(SEARCH_HISTORY, null)
    }

    override fun saveHistoryToSharedPrefs(history: String) {
        val sharedPrefs = context.getSharedPreferences(
            PLAYLIST_MAKER_PREFERENCES,
            Context.MODE_PRIVATE
        )
        sharedPrefs.edit()
            .putString(SEARCH_HISTORY, history)
            .apply()
    }
    companion object {
        private const val SEARCH_HISTORY = "search_history"
        private const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
        private const val DARK_THEME = "dark_theme"
    }


}