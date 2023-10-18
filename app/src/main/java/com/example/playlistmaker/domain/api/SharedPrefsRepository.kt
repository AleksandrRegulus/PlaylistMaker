package com.example.playlistmaker.domain.api

interface SharedPrefsRepository {
    fun getThemeFromSharedPrefs(): Boolean
    fun saveThemeToSharedPrefs(theme: Boolean)

    fun getSearchHistoryFromSharedPrefs(): String?

    fun saveHistoryToSharedPrefs(history: String)
}