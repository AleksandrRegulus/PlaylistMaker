package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.SearchResult
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    override fun searchTracks(expression: String): Flow<SearchResult> {
        return repository.searchTracks(expression).map { result ->
            when (result) {
                is Resource.Success -> {
                    SearchResult.Data(result.data)
                }

                is Resource.Error -> {
                    SearchResult.Error(result.message)
                }
            }
        }
    }

}