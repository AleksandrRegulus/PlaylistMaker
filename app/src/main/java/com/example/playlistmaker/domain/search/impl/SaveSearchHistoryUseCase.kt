package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.api.SharedPrefsRepository
import com.google.gson.Gson

class SaveSearchHistoryUseCase(
    private val repository: SharedPrefsRepository
) {

    fun execute(history: List<Track>) {
        val json = Gson().toJson(history)
        repository.saveHistoryToSharedPrefs(json)
    }
}