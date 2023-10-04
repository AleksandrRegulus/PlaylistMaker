package com.example.playlistmaker.domain.use_case

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.api.SharedPrefsRepository
import com.google.gson.Gson

class GetSearchHistoryUseCase(
    private val repository: SharedPrefsRepository
) {

    fun execute(): ArrayList<Track> {
        val json = repository.getSearchHistoryFromSharedPrefs() ?: return ArrayList()
        val result = ArrayList<Track>()
        result.addAll(Gson().fromJson(json, Array<Track>::class.java))
        return result
    }
}