package com.example.playlistmaker.ui.media.favorite.view_model

import com.example.playlistmaker.domain.search.model.Track

sealed interface FavoriteState {
    object Empty: FavoriteState
    data class FavoriteContent(val tracks: List<Track>): FavoriteState
}