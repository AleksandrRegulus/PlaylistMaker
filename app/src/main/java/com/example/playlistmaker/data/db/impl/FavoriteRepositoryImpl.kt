package com.example.playlistmaker.data.db.impl

import com.example.playlistmaker.data.converters.FavTrackDbConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.db.FavoriteRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val favTrackDbConverter: FavTrackDbConverter,
) : FavoriteRepository {
    override suspend fun addTrackToFav(track: Track) {
        appDatabase.favTrackDao().insertTrackToFav(favTrackDbConverter.map(track))
    }

    override suspend fun deleteTrackFromFav(trackId: Int) {
        appDatabase.favTrackDao().deleteTrackFromFav(trackId)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.favTrackDao().getFavTracks()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> favTrackDbConverter.map(track) }
    }
}