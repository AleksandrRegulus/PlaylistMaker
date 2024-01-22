package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.api.SharedPrefsRepository
import kotlinx.coroutines.flow.Flow

class GetSearchHistoryUseCase(
    private val repository: SharedPrefsRepository,
) {

    fun execute(): Flow<List<Track>> {
        return repository.getSearchHistoryFromSharedPrefs()
    }
}