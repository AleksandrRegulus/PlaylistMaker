package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun addPlaylist(playlist: Playlist): Boolean
    suspend fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addPlaylistsTrack(track: Track, playlist: Playlist): Boolean
}