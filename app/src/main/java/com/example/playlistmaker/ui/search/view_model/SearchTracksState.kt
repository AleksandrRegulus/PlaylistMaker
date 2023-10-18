package com.example.playlistmaker.ui.search.view_model

import com.example.playlistmaker.domain.search.model.Track

sealed interface SearchTracksState {

    object Loading : SearchTracksState

    data class NetworkContent(
        val tracks: List<Track>
    ) : SearchTracksState

    data class HistoryContent(
        val tracks: List<Track>
    ) : SearchTracksState
    object Error : SearchTracksState

    object Empty : SearchTracksState

}