package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.api.SharedPrefsRepository
import com.google.gson.Gson

class GetSearchHistoryUseCase(
    private val repository: SharedPrefsRepository,
    private val gson: Gson
) {

    fun execute(): List<Track> {
        val json = repository.getSearchHistoryFromSharedPrefs() ?: return emptyList()
        return gson.fromJson(json, Array<Track>::class.java).toList()
    }
}