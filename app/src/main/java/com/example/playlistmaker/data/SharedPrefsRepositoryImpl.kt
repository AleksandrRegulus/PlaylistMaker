package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SharedPrefsRepository

class SharedPrefsRepositoryImpl(private var sharedPrefs: SharedPreferences): SharedPrefsRepository {

    override fun getThemeFromSharedPrefs (): Boolean {
        return sharedPrefs.getBoolean(DARK_THEME_KEY, false)
    }

    override fun saveThemeToSharedPrefs(theme: Boolean) {
        sharedPrefs.edit()
            .putBoolean(DARK_THEME_KEY, theme)
            .apply()
    }

    override fun getSearchHistoryFromSharedPrefs(): String? {
        return sharedPrefs.getString(SEARCH_HISTORY_KEY, null)
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