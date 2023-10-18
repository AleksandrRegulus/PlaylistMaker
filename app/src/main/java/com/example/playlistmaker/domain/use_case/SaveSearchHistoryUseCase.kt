package com.example.playlistmaker.domain.use_case

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.api.SharedPrefsRepository
import com.google.gson.Gson

class SaveSearchHistoryUseCase(
    private val repository: SharedPrefsRepository
) {

    fun execute(history: ArrayList<Track>) {
        val json = Gson().toJson(history)
        repository.saveHistoryToSharedPrefs(json)
    }
}