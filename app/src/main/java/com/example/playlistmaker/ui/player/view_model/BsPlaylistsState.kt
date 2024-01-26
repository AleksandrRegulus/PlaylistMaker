package com.example.playlistmaker.ui.player.view_model

import com.example.playlistmaker.domain.playlist.model.Playlist

sealed interface BsPlaylistsState {

    data class playlistsContent(val playlists: List<Playlist>): BsPlaylistsState
    data class trackAdded(val playlistName: String): BsPlaylistsState
    data class trackExists(val playlistName: String): BsPlaylistsState

}