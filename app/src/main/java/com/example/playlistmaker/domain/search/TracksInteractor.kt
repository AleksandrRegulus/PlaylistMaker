package com.example.playlistmaker.domain.search

import kotlinx.coroutines.flow.Flow

interface TracksInteractor {

    fun searchTracks(expression: String): Flow<SearchResult>

}