package com.example.playlistmaker.domain.db.impl

import com.example.playlistmaker.domain.db.PlaylistsInteractor
import com.example.playlistmaker.domain.db.PlaylistsRepository
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val playlistsRepository: PlaylistsRepository
): PlaylistsInteractor {

    override suspend fun addPlaylist(playlist: Playlist): Boolean {
        return playlistsRepository.addPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist): Boolean {
        return playlistsRepository.updatePlaylist(playlist)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getPlaylists()
    }

    override suspend fun getPlaylist(playlistId: Int): Flow<Playlist> {
        return playlistsRepository.getPlaylist(playlistId)
    }

    override suspend fun addPlaylistsTrack(track: Track, playlist: Playlist): Boolean {
        return playlistsRepository.addPlaylistsTrack(track, playlist)
    }

    override suspend fun getPlaylistTracks(trackIdList: List<Int>): Flow<List<Track>> {
        return playlistsRepository.getPlaylistTracks(trackIdList)
    }

    override suspend fun deleteTrackFromPlaylist(playlist: Playlist, deleteTrackId: Int) {
        playlistsRepository.deleteTrackFromPlaylist(playlist, deleteTrackId)
    }

    override suspend fun deletePlaylist(playlist: Playlist): Boolean {
        return playlistsRepository.deletePlaylist(playlist)
    }
}