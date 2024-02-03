package com.example.playlistmaker.data.db.impl

import android.util.Log
import com.example.playlistmaker.data.converters.PlaylistDbConverter
import com.example.playlistmaker.data.converters.PlaylistsTrackDbConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.db.PlaylistsRepository
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val playlistsTrackDbConverter: PlaylistsTrackDbConverter,
    private val gson: Gson,
) : PlaylistsRepository {
    override suspend fun addPlaylist(playlist: Playlist): Boolean {
        val playlists = appDatabase.playlistsDao().getPlaylists()
        val position = playlists.indexOfFirst { it.playlistName == playlist.playlistName }
        return if (position == -1) {
            appDatabase.playlistsDao().insertPlaylist(playlistDbConverter.map(playlist, gson))
            true
        } else false
    }

    override suspend fun updatePlaylist(playlist: Playlist): Boolean {
        appDatabase.playlistsDao().insertPlaylist(playlistDbConverter.map(playlist, gson))
        return true
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistsDao().getPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override suspend fun getPlaylist(playlistId: Int): Flow<Playlist> = flow {
        val playlist = appDatabase.playlistsDao().getPlaylist(playlistId)
        emit(playlistDbConverter.map(playlist, gson))
    }

    override suspend fun addPlaylistsTrack(track: Track, playlist: Playlist): Boolean {
        val updateTrackIDs = playlist.trackIDs.toMutableList()
        updateTrackIDs.add(track.trackId)

        val updatePlaylist =
            playlist.copy(trackIDs = updateTrackIDs, numberOfTracks = playlist.numberOfTracks + 1)
        appDatabase.playlistsDao().insertPlaylist(playlistDbConverter.map(updatePlaylist, gson))

        appDatabase.playlistsTrackDao()
            .insertPlaylistsTrack(playlistsTrackDbConverter.map(track))
        return true
    }

    override suspend fun getPlaylistTracks(trackIdList: List<Int>): Flow<List<Track>> = flow {
        val tracks = appDatabase.playlistsTrackDao().getPlaylistTracks(trackIdList)
        val favTracks = appDatabase.favTrackDao().getFavTracksIDs()
        emit(tracks.map { playlistsTrackDbConverter.map(it, isFavorite = it.trackId in favTracks) })
    }

    override suspend fun deleteTrackFromPlaylist(playlist: Playlist, deleteTrackId: Int) {
        val updatePlaylist = playlist.copy(
            trackIDs = playlist.trackIDs.filter { it != deleteTrackId },
            numberOfTracks = playlist.numberOfTracks - 1
        )
        appDatabase.playlistsDao().insertPlaylist(playlistDbConverter.map(updatePlaylist, gson))

        deleteNotUsedTracks(listOf(deleteTrackId))
    }

    override suspend fun deletePlaylist(playlist: Playlist): Boolean {
        appDatabase.playlistsDao().deletePlaylist(playlist.playlistId)
        deleteNotUsedTracks(playlist.trackIDs)
        return true
    }

    private suspend fun deleteNotUsedTracks(trackIdList: List<Int>) {
        val playlists = appDatabase.playlistsDao().getPlaylists()
        trackIdList.forEach nextTrackId@{ trackId ->
            val trackIdString = trackId.toString()
            playlists.forEach { playlist ->
                if (playlist.trackIDs.contains(trackIdString)) return@nextTrackId
            }
            appDatabase.playlistsTrackDao().deleteTrack(trackId)
        }
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConverter.map(playlist, gson) }
    }
}