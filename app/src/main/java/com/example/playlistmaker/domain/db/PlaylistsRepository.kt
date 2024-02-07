package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun addPlaylist(playlist: Playlist): Boolean
    suspend fun updatePlaylist(playlist: Playlist): Boolean
    suspend fun getPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylist(playlistId: Int): Flow<Playlist>
    suspend fun addPlaylistsTrack(track: Track, playlist: Playlist): Boolean
    suspend fun getPlaylistTracks(trackIdList: List<Int>): Flow<List<Track>>
    suspend fun deleteTrackFromPlaylist(playlist: Playlist, deleteTrackId: Int)
    suspend fun deletePlaylist(playlist: Playlist): Boolean
}