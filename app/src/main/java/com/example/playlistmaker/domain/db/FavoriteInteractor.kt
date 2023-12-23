package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteInteractor {

    suspend fun addTrackToFav(track: Track)
    suspend fun deleteTrackFromFav(trackId: Int)
    fun favoriteTracks(): Flow<List<Track>>
}