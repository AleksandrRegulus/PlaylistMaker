package com.example.playlistmaker.data.db.impl

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
): PlaylistsRepository {
    override suspend fun addPlaylist(playlist: Playlist): Boolean {
        val playlists = appDatabase.playlistsDao().getPlaylists()
        val position = playlists.indexOfFirst { it.playlistName == playlist.playlistName }
        return if (position == -1) {
            appDatabase.playlistsDao().insertPlaylist(playlistDbConverter.map(playlist, gson))
            true
        } else false
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistsDao().getPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override suspend fun addPlaylistsTrack(track: Track, playlist: Playlist): Boolean {
        val updateTrackIDs = playlist.trackIDs.toMutableList()
        updateTrackIDs.add(track.trackId.toString())

        val updatePlaylist = playlist.copy(trackIDs = updateTrackIDs, numberOfTracks = playlist.numberOfTracks + 1 )
        appDatabase.playlistsDao().insertPlaylist(playlistDbConverter.map(updatePlaylist, gson))

        val id = appDatabase.playlistsTrackDao().insertPlaylistsTrack(playlistsTrackDbConverter.map(track))
        return id > 0
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist ->  playlistDbConverter.map(playlist, gson)}
    }
}