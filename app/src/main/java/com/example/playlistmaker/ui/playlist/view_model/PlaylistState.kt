package com.example.playlistmaker.ui.playlist.view_model

import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track

data class PlaylistState(
    val playlist: Playlist,
    val tracks: List<Track>,
    val tracksDuration: String,
    val numTracks: String
)
