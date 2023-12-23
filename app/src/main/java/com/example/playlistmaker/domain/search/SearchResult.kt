package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track

sealed interface SearchResult {

    data class Error(val message: String?): SearchResult
    data class Data(val tracks: List<Track>?): SearchResult
}