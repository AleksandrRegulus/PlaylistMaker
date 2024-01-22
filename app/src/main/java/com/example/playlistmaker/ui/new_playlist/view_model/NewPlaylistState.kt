package com.example.playlistmaker.ui.new_playlist.view_model

interface NewPlaylistState {
    data class PlaylistCreated(val playlistName: String): NewPlaylistState

    data class Error(val playlistName: String): NewPlaylistState

}