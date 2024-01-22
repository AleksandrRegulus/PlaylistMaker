package com.example.playlistmaker.domain.db.impl

import com.example.playlistmaker.domain.db.FavoriteInteractor
import com.example.playlistmaker.domain.db.FavoriteRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class FavoriteInteractorImpl(
    private val favoriteRepository: FavoriteRepository
): FavoriteInteractor {

    override suspend fun addTrackToFav(track: Track) {
        favoriteRepository.addTrackToFav(track)
    }

    override suspend fun deleteTrackFromFav(trackId: Int) {
        favoriteRepository.deleteTrackFromFav(trackId)
    }

    override fun favoriteTracks(): Flow<List<Track>> {
        return favoriteRepository.getFavoriteTracks()
    }
}