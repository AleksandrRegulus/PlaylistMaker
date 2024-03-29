package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {

    suspend fun addTrackToFav(track: Track)
    suspend fun deleteTrackFromFav(trackId: Int)
    fun getFavoriteTracks(): Flow<List<Track>>
}