package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface SharedPrefsRepository {
    fun getThemeFromSharedPrefs(): Boolean
    fun saveThemeToSharedPrefs(theme: Boolean)

    fun getSearchHistoryFromSharedPrefs(): Flow<List<Track>>

    fun saveHistoryToSharedPrefs(history: String)
}