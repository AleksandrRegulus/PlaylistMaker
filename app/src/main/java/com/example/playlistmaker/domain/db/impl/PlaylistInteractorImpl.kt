package com.example.playlistmaker.domain.db.impl

import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.db.PlaylistsRepository
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistsRepository: PlaylistsRepository
): PlaylistInteractor {

    override suspend fun addPlaylist(playlist: Playlist): Boolean {
        return playlistsRepository.addPlaylist(playlist)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getPlaylists()
    }

    override suspend fun addPlaylistsTrack(track: Track, playlist: Playlist): Boolean {
        return playlistsRepository.addPlaylistsTrack(track, playlist)
    }
}