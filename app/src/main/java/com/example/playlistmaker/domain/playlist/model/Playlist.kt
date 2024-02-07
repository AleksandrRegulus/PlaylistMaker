package com.example.playlistmaker.domain.playlist.model

data class Playlist(
    val playlistId: Int,
    val playlistName: String,
    val playlistDescription: String,
    val posterUri: String,
    val trackIDs: List<Int>,
    val numberOfTracks: Int,
)
