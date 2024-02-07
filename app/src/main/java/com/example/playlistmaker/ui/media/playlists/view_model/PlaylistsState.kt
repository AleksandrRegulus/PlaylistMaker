package com.example.playlistmaker.ui.media.playlists.view_model

import com.example.playlistmaker.domain.playlist.model.Playlist

interface PlaylistsState {

    object Empty: PlaylistsState
    data class PlaylistContent(val playlists: List<Playlist>): PlaylistsState
}